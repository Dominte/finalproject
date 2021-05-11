package com.example.finalproject.controllers;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.TestDto;
import com.example.finalproject.services.TestService;
import com.example.finalproject.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseDto> createTest(@RequestBody @Validated TestDto testDto){
        return testService.createTest(testDto);
    }

}
