package com.example.finalproject.services;

import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.SubmitAnswerDto;
import com.example.finalproject.models.Answer;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.AnswerRepository;
import com.example.finalproject.repositories.QuestionRepository;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private TestRepository testRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;

    @Autowired
    public AnswerService(UserRepository userRepository, QuestionRepository questionRepository, TestRepository testRepository, AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResponseDto> submitAnswer(SubmitAnswerDto submitAnswerDto) {

        if (userRepository.findUserByRegistrationCode(submitAnswerDto.getStudentRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Student does not exist"), HttpStatus.BAD_REQUEST);
        }
        if (testRepository.findById(submitAnswerDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }
        if (questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Question with this index on this test does not exist"), HttpStatus.BAD_REQUEST);
        }
        Long studentId = userRepository.findUserByRegistrationCode(submitAnswerDto.getStudentRegistrationCode()).get().getId();
        Long questionId = questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).get().getId();
        if (answerRepository.findAnswerByStudentIdAndQuestionId(studentId, questionId).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Question already answered by this student"), HttpStatus.CONFLICT);
        }

        try {
            Answer answer = Answer.builder()
                    .text(submitAnswerDto.getText())
                    .student(userRepository.findUserByRegistrationCode(submitAnswerDto.getStudentRegistrationCode()).get())
                    .question(questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).get())
                    .build();

            answerRepository.save(answer);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Answer added"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
