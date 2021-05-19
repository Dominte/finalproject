package com.example.finalproject.controllers;

import com.example.finalproject.dtos.QuestionDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.TestDto;
import com.example.finalproject.models.Question;
import com.example.finalproject.services.QuestionService;
import com.example.finalproject.services.TestService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/question")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/add")
    @SneakyThrows
    public ResponseEntity<ResponseDto> addQuestion(@RequestBody @Validated QuestionDto questionDto, @RequestHeader(name = "Authorization") String token) {
        return questionService.addQuestionToTest(questionDto, token);
    }

    @GetMapping("")
    @SneakyThrows
    public ResponseEntity<?> getQuestionsFromTest(@RequestParam Long testId) {
        return questionService.getQuestionsFromTest(testId);
    }


}
