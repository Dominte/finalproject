package com.example.finalproject.services;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.exceptions.UserAlreadyExistsException;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.GeneratePassword;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> registerUser(RegisterDto registerDto) {
        if (userRepository.findUserByEmail(registerDto.getEmail()).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Email is not unique"), HttpStatus.CONFLICT);
        }

        if (userRepository.findUserByRegistrationCode(registerDto.getRegistrationCode()).isPresent()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Registration code is not unique"), HttpStatus.CONFLICT);
        }


        try {
            User user = User.builder()
                    .firstName(registerDto.getFirstName().trim())
                    .lastName(registerDto.getLastName().trim())
                    .email(registerDto.getEmail().trim())
                    .registrationCode(registerDto.getRegistrationCode().trim())
                    .role(registerDto.getRole())
                    .username(registerDto.getLastName().toLowerCase().trim().replaceAll("\\s", ".") + "." + registerDto.getFirstName().toLowerCase().trim().replaceAll("\\s", "."))
                    .password(GeneratePassword.generate(20)).build();

            userRepository.save(user);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED, "User registered successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }


    @SneakyThrows
    public ResponseEntity<ResponseDto> modifyPassword(UpdatePasswordDto updatePasswordDto) {
        if (userRepository.findUserByRegistrationCode(updatePasswordDto.getRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Student does not exist"), HttpStatus.CONFLICT);
        }
        if(!userRepository.findUserByRegistrationCode(updatePasswordDto.getRegistrationCode()).get().getPassword().equals(updatePasswordDto.getPassword())){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Password does not match"), HttpStatus.CONFLICT);
        }

        try {
            userRepository.setPasswordByRegistrationCode(updatePasswordDto.getRegistrationCode(), updatePasswordDto.getNewPassword());
            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Password has been changed successfully"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }



}
