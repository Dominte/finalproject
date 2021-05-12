package com.example.finalproject.repositories;

import com.example.finalproject.models.Answer;
import com.example.finalproject.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
