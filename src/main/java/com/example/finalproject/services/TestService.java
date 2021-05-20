package com.example.finalproject.services;

import com.example.finalproject.dtos.*;
import com.example.finalproject.models.Test;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.FormattedDateMatcher;
import com.example.finalproject.utils.JwtTokenUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {

    private TestRepository testRepository;
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private final FormattedDateMatcher dateMatcher = new FormattedDateMatcher();

    @Autowired
    public TestService(TestRepository testRepository, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.testRepository = testRepository;
    }

    @SneakyThrows
    public ResponseEntity<?> getCreatedTests(String token) {
        token = token.substring(7);

        if (userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Teacher does not exist"), HttpStatus.BAD_REQUEST);
        }

        try {
            User teacher = userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).get();
            List<Test> dbTests = testRepository.findAllByTeacherId(teacher.getId());

            List<CreatedTestDto> tests = new ArrayList<>();

            for (Test dbTest : dbTests) {
                CreatedTestDto createdTestDto = new CreatedTestDto();

                createdTestDto.setTestDate(dbTest.getTestDate());
                createdTestDto.setTitle(dbTest.getTitle());
                createdTestDto.setFinishingHour(dbTest.getFinishingHour());
                createdTestDto.setStartingHour(dbTest.getStartingHour());

                tests.add(createdTestDto);
            }

            return new ResponseEntity<>(tests, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }


    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> createTest(TestDto testDto, String token) {
        token = token.substring(7);

        if (userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Teacher does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (!dateMatcher.matches(testDto.getTestDate())) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Date format should be yyyy-mm-dd"), HttpStatus.BAD_REQUEST);
        }

        if (testRepository.findTestByNameAndDate(testDto.getTitle(), LocalDate.parse(testDto.getTestDate())).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test already exists on that date"), HttpStatus.CONFLICT);
        }

        if (LocalDate.parse(testDto.getTestDate()).compareTo(LocalDate.now()) < 0) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Can't set test date in the past"), HttpStatus.I_AM_A_TEAPOT);
        }

        try {
            Test test;
            test = Test.builder()
                    .teacher(userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).get())
                    .title(testDto.getTitle())
                    .testDate(LocalDate.parse(testDto.getTestDate()))
                    .build();

            if (testDto.getFinishingHour() != null) {
                if (!updateFinishingHour(testDto.getFinishingHour(), test)) {
                    return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Finishing hour format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
                }
            }

            if (testDto.getStartingHour() != null) {
                if (!updateFinishingHour(testDto.getFinishingHour(), test)) {
                    return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Starting hour format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
                }
            }

            testRepository.save(test);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Test created"), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> updateTest(UpdateTestDto updateTestDto, String token) {
        token = token.substring(7);

        if (testRepository.findById(updateTestDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        Test test = testRepository.findById(updateTestDto.getTestId()).get();

        if (!testRepository.findById(updateTestDto.getTestId()).get().getTeacher().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "You do not own this test"), HttpStatus.CONFLICT);
        }


        if (updateTestDto.getNewFinishingHour() != null) {
            if (!updateFinishingHour(updateTestDto.getNewFinishingHour(), test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Finishing hour format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
            }
        }
        if (updateTestDto.getNewStartingHour() != null) {
            if (!updateTestStartingHour(updateTestDto.getNewStartingHour(), test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Starting hour format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
            }
        }
        if (updateTestDto.getNewTestDate() != null) {
            if (!dateMatcher.matches(updateTestDto.getNewTestDate())) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Date format should be yyyy-mm-dd"), HttpStatus.BAD_REQUEST);
            }
            if (LocalDate.parse(updateTestDto.getNewTestDate()).compareTo(LocalDate.now()) < 0) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Cannot set test date in the past"), HttpStatus.I_AM_A_TEAPOT);
            }
            if (!updateTestDate(updateTestDto.getNewTestDate(), test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Date format should be yyyy-mm-dd"), HttpStatus.BAD_REQUEST);
            }
        }
        if (updateTestDto.getNewTitle() != null) {
            if (updateTestDto.getNewTestDate() != null) {
                if (testRepository.findTestByNameAndDate(updateTestDto.getNewTitle(), LocalDate.parse(updateTestDto.getNewTestDate())).isPresent()) {
                    return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "A test with this title already exists on that date"), HttpStatus.BAD_REQUEST);
                }
            } else if (testRepository.findTestByNameAndDate(updateTestDto.getNewTitle(), test.getTestDate()).isPresent()) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "A test with this title already exists on that date"), HttpStatus.BAD_REQUEST);
            }

            if (!updateTestTitle(updateTestDto.getNewTitle(), test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test title couldn't be added"), HttpStatus.BAD_REQUEST);
            }
        }

        testRepository.save(test);
        return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Test updated"), HttpStatus.ACCEPTED);
    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> signupToTest(SignupTestDto signupTestDto, String token) {
        token = token.substring(7);

        if (testRepository.findById(signupTestDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        try {
            User user = userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).get();
            Test test = testRepository.findById(signupTestDto.getTestId()).get();

            ArrayList<User> studentList = new ArrayList<>(test.getStudents());
            studentList.add(user);

            test.setStudents(studentList);
            testRepository.save(test);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Signed up successfully"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    @Modifying
    @Transactional
    @SneakyThrows
    public ResponseEntity<ResponseDto> deleteTest(Long testId, String token) {
        token = token.substring(7);

        if (testRepository.findById(testId).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        Test test = testRepository.findById(testId).get();

        if (!test.getTeacher().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "You do not own this test"), HttpStatus.BAD_REQUEST);
        }

        try {

            testRepository.delete(test);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Deleted test successfully"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }


    @Modifying
    @Transactional
    @SneakyThrows
    public ResponseEntity<ResponseDto> assignStudentToTest(AssignStudentDto assignStudentDto, String token) {
        token = token.substring(7);

        if (userRepository.findUserByRegistrationCode(assignStudentDto.getStudentRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Student does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (testRepository.findById(assignStudentDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        Test test = testRepository.findById(assignStudentDto.getTestId()).get();

        if (!test.getTeacher().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "You do not own this test"), HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < test.getStudents().size(); i++) {
            if (test.getStudents().get(i).getRegistrationCode().equals(assignStudentDto.getStudentRegistrationCode()))
                return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Student already assigned to this test"), HttpStatus.CONFLICT);
        }

        try {
            ArrayList<User> studentList = new ArrayList<>(test.getStudents());
            studentList.add(userRepository.findUserByRegistrationCode(assignStudentDto.getStudentRegistrationCode()).get());

            test.setStudents(studentList);
            testRepository.save(test);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Assigned student to this test"), HttpStatus.ACCEPTED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean canSubmitAnswer(LocalTime startingHour, LocalTime finishingHour, LocalDate testDate) {

        if (LocalDate.now().getYear() != testDate.getYear()) {
            return false;
        }
        if (LocalDate.now().getMonth() != testDate.getMonth()) {
            return false;
        }
        if (LocalDate.now().getDayOfMonth() != testDate.getDayOfMonth()) {
            return false;
        }


        if (LocalTime.now().isBefore(startingHour))
            return false;


        return true;
    }

    private boolean updateFinishingHour(String finishingHour, Test test) {
        try {
            test.setFinishingHour(LocalTime.parse(finishingHour));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestStartingHour(String startingHour, Test test) {
        try {
            test.setStartingHour(LocalTime.parse(startingHour));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestDate(String testDate, Test test) {
        try {
            test.setTestDate(LocalDate.parse(testDate));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestTitle(String testTitle, Test test) {
        try {
            test.setTitle(testTitle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
