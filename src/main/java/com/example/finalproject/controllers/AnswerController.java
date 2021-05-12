package com.example.finalproject.controllers;

import com.example.finalproject.dtos.QuestionDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.SubmitAnswerDto;
import com.example.finalproject.services.AnswerService;
import com.example.finalproject.services.QuestionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/answer")
public class AnswerController {

    private AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/add")
    @SneakyThrows
    public ResponseEntity<ResponseDto> submitAnswer(@RequestBody @Validated SubmitAnswerDto submitAnswerDto){
        return answerService.submitAnswer(submitAnswerDto);
    }
}
