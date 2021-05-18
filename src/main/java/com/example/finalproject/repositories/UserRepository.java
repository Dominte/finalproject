package com.example.finalproject.repositories;



import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.models.Question;
import com.example.finalproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM users u WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM users u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT u from users u WHERE u.registrationCode = :registration_code")
    Optional<User> findUserByRegistrationCode(@Param("registration_code") String registration_code);


    @Modifying
    @Transactional
    @Query("UPDATE users u SET u.password= :password WHERE u.registrationCode = :registration_code")
    void setPasswordByRegistrationCode(@Param("registration_code") String registration_code, @Param("password") String password);
}