package com.example.finalproject.services;

import com.example.finalproject.dtos.QuestionDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.models.Question;
import com.example.finalproject.repositories.QuestionRepository;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private TestRepository testRepository;
    private QuestionRepository questionRepository;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, JwtTokenUtil jwtTokenUtil, TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.testRepository = testRepository;
    }

    public ResponseEntity<ResponseDto> addQuestionToTest(QuestionDto questionDto, String token) {
        token = token.substring(7);

        if (testRepository.findById(questionDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (!testRepository.findById(questionDto.getTestId()).get().getTeacher().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "You do not own this test"), HttpStatus.CONFLICT);
        }

        try {
            int questionIndex = (questionRepository.countQuestionsByTestId(questionDto.getTestId())) + 1;

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
