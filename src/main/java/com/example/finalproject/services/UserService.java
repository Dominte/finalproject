package com.example.finalproject.services;

import com.example.finalproject.dtos.*;
import com.example.finalproject.exceptions.UserAlreadyExistsException;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.GeneratePassword;
import com.example.finalproject.utils.JwtTokenUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Random;

@Service
public class UserService {

    private UserRepository userRepository;

    private JwtTokenUtil jwtTokenUtil;

    private UserServiceSecurity userServiceSecurity;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, UserServiceSecurity userServiceSecurity) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.userServiceSecurity = userServiceSecurity;
    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> registerUser(RegisterDto registerDto) {
        if (userRepository.findUserByEmail(registerDto.getEmail()).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Email is not unique"), HttpStatus.CONFLICT);
        }

        if (userRepository.findUserByRegistrationCode(registerDto.getRegistrationCode()).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Registration code is not unique"), HttpStatus.CONFLICT);
        }
        int increment = 1;
        String username = registerDto.getLastName().toLowerCase().trim().replaceAll("\\s", ".") + "." + registerDto.getFirstName().toLowerCase().trim().replaceAll("\\s", ".");
        if (userRepository.findUserByRegistrationCode(registerDto.getRegistrationCode()).isEmpty() && userRepository.findUserByUsername(username).isPresent()) {

            while (userRepository.findUserByUsername(username + increment).isPresent())
                increment++;
        }


        try {
            User user = User.builder()
                    .firstName(registerDto.getFirstName().trim())
                    .lastName(registerDto.getLastName().trim())
                    .email(registerDto.getEmail().trim())
                    .registrationCode(registerDto.getRegistrationCode().trim())
                    .role(registerDto.getRole())
                    .username(username + increment)
                    .password(GeneratePassword.generate(20)).build();

            userRepository.save(user);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "User registered successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }


    @SneakyThrows
    public ResponseEntity<ResponseDto> modifyPassword(UpdatePasswordDto updatePasswordDto,String token) {
        token=token.substring(7);

        if (userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Student does not exist"), HttpStatus.CONFLICT);
        }
        if (!userRepository.findUserByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token)).get().getPassword().equals(updatePasswordDto.getPassword())) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Password does not match"), HttpStatus.CONFLICT);
        }

        try {
            userRepository.setPasswordByRegistrationCode(jwtTokenUtil.getRegistrationCodeFromToken(token), updatePasswordDto.getNewPassword());
            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Password has been changed successfully"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    public ResponseEntity<?> loginUser(LoginDto loginDto) {
        if (userRepository.findUserByUsername(loginDto.getUsername()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Wrong username or password"), HttpStatus.CONFLICT);
        }
        if (!userRepository.findUserByUsername(loginDto.getUsername()).get().getPassword().equals(loginDto.getPassword())) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Wrong username or password"), HttpStatus.CONFLICT);
        }

        try {
            final String Token = jwtTokenUtil.generateToken(userServiceSecurity.loadUserByUsername(loginDto.getUsername()));
            return new ResponseEntity<>(new TokenDto(Token), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }


}
