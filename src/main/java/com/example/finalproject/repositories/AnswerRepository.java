package com.example.finalproject.repositories;

import com.example.finalproject.models.Answer;
import com.example.finalproject.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a from answers a WHERE a.student.id = :student_id AND a.question.id = :question_id")
    Optional<Answer> findAnswerByStudentIdAndQuestionId(@Param("student_id") Long student_id, @Param("question_id") Long question_id);

}
