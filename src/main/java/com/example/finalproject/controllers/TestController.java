package com.example.finalproject.controllers;

import com.example.finalproject.dtos.*;
import com.example.finalproject.services.TestService;
import com.example.finalproject.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/test")
public class TestController {

    private TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping("/create")
    @SneakyThrows
    public ResponseEntity<ResponseDto> createTest(@RequestBody @Validated TestDto testDto, @RequestHeader(name = "Authorization") String token) {
        return testService.createTest(testDto, token);
    }

    @GetMapping("/created")
    @SneakyThrows
    public ResponseEntity<?> getCreatedTests(@RequestHeader(name = "Authorization") String token) {
        return testService.getCreatedTests(token);
    }

    @PostMapping("/assign")
    @SneakyThrows
    public ResponseEntity<ResponseDto> assignStudentToTest(@RequestBody @Validated AssignStudent assignStudent) {
        return testService.assignStudentToTest(assignStudent);
    }

    @PutMapping("/update")
    @SneakyThrows
    public ResponseEntity<ResponseDto> updateTest(@RequestBody @Validated UpdateTestDto updateTestDto, @RequestHeader(name = "Authorization") String token) {
        return testService.updateTest(updateTestDto, token);
    }

}
