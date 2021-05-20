package com.example.finalproject.services;

import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.SubmitAnswerDto;
import com.example.finalproject.models.Answer;
import com.example.finalproject.models.Question;
import com.example.finalproject.models.Test;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.AnswerRepository;
import com.example.finalproject.repositories.QuestionRepository;
import com.example.finalproject.repositories.TestRepository;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AnswerService {

    private TestRepository testRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AnswerService(JwtTokenUtil jwtTokenUtil, UserRepository userRepository, QuestionRepository questionRepository, TestRepository testRepository, AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> submitAnswer(SubmitAnswerDto submitAnswerDto, String token) {
        token = token.substring(7);

        if (userRepository.findUserByUsername(jwtTokenUtil.getUsernameFromToken(token)).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Student does not exist"), HttpStatus.BAD_REQUEST);
        }
        if (testRepository.findById(submitAnswerDto.getTestId()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Test does not exist"), HttpStatus.BAD_REQUEST);
        }
        if (questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Question with this index on this test does not exist"), HttpStatus.BAD_REQUEST);
        }

        Test test = testRepository.findById(submitAnswerDto.getTestId()).get();

        if (canSubmitAnswer(test.getStartingHour(), test.getFinishingHour(), test.getTestDate()).getStatusCode()!=HttpStatus.ACCEPTED) {
            return canSubmitAnswer(test.getStartingHour(), test.getFinishingHour(), test.getTestDate());
        }

        Long studentId = userRepository.findUserByUsername(jwtTokenUtil.getUsernameFromToken(token)).get().getId();
        Long questionId = questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).get().getId();
        if (answerRepository.findAnswerByStudentIdAndQuestionId(studentId, questionId).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Question already answered by this student"), HttpStatus.CONFLICT);
        }

        try {
            Answer answer = Answer.builder()
                    .text(submitAnswerDto.getText())
                    .student(userRepository.findUserByUsername(jwtTokenUtil.getUsernameFromToken(token)).get())
                    .question(questionRepository.findQuestionByTestIdAndQuestionIndex(testRepository.findById(submitAnswerDto.getTestId()).get().getId(), submitAnswerDto.getQuestionIndex()).get())
                    .build();

            answerRepository.save(answer);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "Answer added"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> canSubmitAnswer(LocalTime startingHour, LocalTime finishingHour, LocalDate testDate) {

        if (LocalDate.now().getYear() != testDate.getYear() || LocalDate.now().getMonth() != testDate.getMonth() || LocalDate.now().getDayOfMonth() != testDate.getDayOfMonth()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test is not held today"), HttpStatus.CONFLICT);
        }

        if (LocalTime.now().isBefore(startingHour))
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test has not started yet"), HttpStatus.CONFLICT);

        if(LocalTime.now().isAfter(finishingHour))
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Test finished already"), HttpStatus.CONFLICT);

        return new ResponseEntity<>("", HttpStatus.ACCEPTED);


    }
}
