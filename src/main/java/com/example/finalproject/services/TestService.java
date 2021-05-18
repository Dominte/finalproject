package com.example.finalproject.services;

import com.example.finalproject.dtos.AssignStudent;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.TestDto;
import com.example.finalproject.dtos.UpdateTestDto;
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
import java.util.ArrayList;

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
    public ResponseEntity<ResponseDto> createTest(TestDto testDto, String token) {
        token = token.substring(7);

        if (userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Teacher does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (!dateMatcher.matches(testDto.getTestDay())) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Date format should be yyyy-mm-dd"), HttpStatus.BAD_REQUEST);
        }

        if (testRepository.findTestByNameAndDate(testDto.getTitle(), LocalDate.parse(testDto.getTestDay())).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test already exists on that date"), HttpStatus.CONFLICT);
        }

        if (LocalDate.parse(testDto.getTestDay()).compareTo(LocalDate.now()) < 0) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Can't set test date in the past"), HttpStatus.I_AM_A_TEAPOT);
        }

        try {
            Test test;
            test = Test.builder()
                    .teacher(userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).get())
                    .title(testDto.getTitle())
                    .testDate(LocalDate.parse(testDto.getTestDay()))
                    .build();

            testRepository.save(test);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Test created"), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResponseDto> updateTest(UpdateTestDto updateTestDto,String token){

        if (testRepository.findById(updateTestDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (!testRepository.findById(updateTestDto.getTestId()).get().getTeacher().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "You do not own this test"), HttpStatus.CONFLICT);
        }

        if(updateTestDto.getNewDuration().isEmpty()){
            System.out.println("");
        }

        try {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Test created"), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    public void updateTestDuration(UpdateTestDto updateTestDto,String token){
        token=token.substring(7);

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
}
