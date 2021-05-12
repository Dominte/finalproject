package com.example.finalproject.repositories;

import com.example.finalproject.models.Question;
import com.example.finalproject.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q from questions q WHERE q.text = :text AND q.test.id = :id")
    Optional<Question> findQuestionByTestIdAndText(@Param("id") Long id,@Param("text") String text);


    @Query("SELECT q from questions q WHERE q.questionIndex = :question_index AND q.test.id = :id")
    Optional<Question> findQuestionByTestIdAndQuestionIndex(@Param("id") Long id,@Param("question_index") int question_index);

    @Query("SELECT count (q) from questions q WHERE q.test.id = :id")
    int findQuestionsByTestId(@Param("id") Long id);



}