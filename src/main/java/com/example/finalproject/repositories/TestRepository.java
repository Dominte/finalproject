package com.example.finalproject.repositories;

import com.example.finalproject.models.Test;
import com.example.finalproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    @Query("SELECT t from tests t WHERE t.title = :title AND t.testDate = :test_date")
    Optional<Test> findTestByNameAndDate(@Param("title") String title, @Param("test_date") LocalDate test_date);

    @Query("SELECT t FROM tests t WHERE t.teacher.id = :teacher_id")
    List<Test> findAllByTeacherId(@Param("teacher_id") Long teacher_id);

}
