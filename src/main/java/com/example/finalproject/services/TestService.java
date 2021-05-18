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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
                createdTestDto.setDuration(dbTest.getDuration());
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


        if (updateTestDto.getNewDuration() != null) {
            if (!updateTestDuration(updateTestDto, test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Duration format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
            }
        }
        if (updateTestDto.getNewStartingHour() != null) {
            if (!updateTestStartingHour(updateTestDto, test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Starting hour format should be HH:mm and be in a valid timeframe"), HttpStatus.BAD_REQUEST);
            }
        }
        if (updateTestDto.getNewTestDate() != null) {
            if (LocalDate.parse(updateTestDto.getNewTestDate()).compareTo(LocalDate.now()) < 0) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Can't set test date in the past"), HttpStatus.I_AM_A_TEAPOT);
            }
            if (!updateTestDate(updateTestDto, test)) {
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

            if (!updateTestTitle(updateTestDto, test)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Title "), HttpStatus.BAD_REQUEST);
            }
        }

        testRepository.save(test);
        return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Test updated"), HttpStatus.ACCEPTED);


    }

    @Modifying
    @Transactional
    @SneakyThrows
    public ResponseEntity<ResponseDto> assignStudentToTest(AssignStudent assignStudent) {

        if (userRepository.findUserByRegistrationCode(assignStudent.getStudentRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Student does not exist"), HttpStatus.BAD_REQUEST);
        }
        Test test = testRepository.findTestByNameAndDate(assignStudent.getTestTitle(), LocalDate.parse(assignStudent.getTestDay())).get();

        for (int i = 0; i < test.getStudents().size(); i++) {
            if (test.getStudents().get(i).getRegistrationCode().equals(assignStudent.getStudentRegistrationCode()))
                return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Student already assigned to this test"), HttpStatus.CONFLICT);
        }


        if (testRepository.findTestByNameAndDate(assignStudent.getTestTitle(), LocalDate.parse(assignStudent.getTestDay())).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "There is no test with this title on that date"), HttpStatus.BAD_REQUEST);
        }
        try {
            ArrayList<User> studentList = new ArrayList<>(test.getStudents());
            studentList.add(userRepository.findUserByRegistrationCode(assignStudent.getStudentRegistrationCode()).get());


            test.setStudents(studentList);
            testRepository.save(test);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Assigned student to this test"), HttpStatus.ACCEPTED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean updateTestDuration(UpdateTestDto updateTestDto, Test test) {
        try {
            test.setDuration(LocalTime.parse(updateTestDto.getNewDuration()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestStartingHour(UpdateTestDto updateTestDto, Test test) {
        try {
            test.setStartingHour(LocalTime.parse(updateTestDto.getNewStartingHour()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestDate(UpdateTestDto updateTestDto, Test test) {
        try {
            test.setTestDate(LocalDate.parse(updateTestDto.getNewTestDate()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateTestTitle(UpdateTestDto updateTestDto, Test test) {
        try {
            test.setTitle(updateTestDto.getNewTitle());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
