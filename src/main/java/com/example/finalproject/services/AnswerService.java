package com.example.finalproject.services;

import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.repositories.AnswerRepository;
import com.example.finalproject.repositories.QuestionRepository;
import com.example.finalproject.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private TestRepository testRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;

    @Autowired
    public AnswerService(QuestionRepository questionRepository, TestRepository testRepository, AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    public ResponseEntity<ResponseDto> submitAnswer(){

        try{
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);

        }
        catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
