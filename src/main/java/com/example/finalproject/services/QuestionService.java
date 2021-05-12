package com.example.finalproject.services;

import com.example.finalproject.dtos.QuestionDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.models.Question;
import com.example.finalproject.repositories.QuestionRepository;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private TestRepository testRepository;
    private QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    public ResponseEntity<ResponseDto> addQuestionToTest(QuestionDto questionDto) {

        if (testRepository.findById(questionDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        try {
            int questionIndex = (questionRepository.findQuestionsByTestId(questionDto.getTestId())) + 1;

            Question question = Question.builder()
                    .questionIndex(questionIndex)
                    .test(testRepository.findById(questionDto.getTestId()).get())
                    .text(questionDto.getText())
                    .build();

            questionRepository.save(question);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Added question to test"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }
}
