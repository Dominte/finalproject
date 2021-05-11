package com.example.finalproject.services;

import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.TestDto;
import com.example.finalproject.models.Test;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.FormattedDateMatcher;
import com.example.finalproject.utils.GeneratePassword;
import lombok.SneakyThrows;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDate;

@Service
public class TestService {

    private TestRepository testRepository;
    private UserRepository userRepository;
    private final FormattedDateMatcher dateMatcher = new FormattedDateMatcher();

    @Autowired
    public TestService(TestRepository testRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> createTest(TestDto testDto){
        if (userRepository.findUserByRegistrationCode(testDto.getTeacherRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Teacher does not exist"), HttpStatus.BAD_REQUEST);
        }

        if(!dateMatcher.matches(testDto.getTestDay())){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Date format should be yyyy-mm-dd"), HttpStatus.BAD_REQUEST);
        }

        if (testRepository.findTestByNameAndDate(testDto.getTitle(), LocalDate.parse(testDto.getTestDay())).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test already exists on that date"), HttpStatus.CONFLICT);
        }

        if(LocalDate.parse(testDto.getTestDay()).compareTo(LocalDate.now()) < 0){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Can't set test date in the past"), HttpStatus.I_AM_A_TEAPOT);
        }

        try {
            Test test;
            test = Test.builder()
                    .teacher(userRepository.findUserByRegistrationCode(testDto.getTeacherRegistrationCode()).get())
                    .title(testDto.getTitle())
                    .testDate(LocalDate.parse(testDto.getTestDay()))
                    .build();

            testRepository.save(test);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Test created"), HttpStatus.CREATED);

        }
        catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResponseDto> assignStudentToTest(){
        return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
    }
}
