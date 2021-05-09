package com.example.finalproject.services;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.exceptions.UserAlreadyExistsException;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.GeneratePassword;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    public void registerUser(RegisterDto registerDto) {
        if (userRepository.findUserByEmail(registerDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is not unique");
        }

        User user = User.builder()
                .firstName(registerDto.getFirstName().trim())
                .lastName(registerDto.getLastName().trim())
                .email(registerDto.getEmail().trim())
                .registrationCode(registerDto.getRegistrationCode().trim())
                .role(registerDto.getRole())
                .username(registerDto.getLastName().toLowerCase().trim().replaceAll("\\s", ".")+"."+registerDto.getFirstName().toLowerCase().trim().replaceAll("\\s", "."))
                .password(GeneratePassword.generate(20)).build();

        userRepository.save(user);
    };

    @SneakyThrows
    public void modifyPassword(UpdatePasswordDto updatePasswordDto){
        userRepository.setPasswordByRegistrationCode(updatePasswordDto.getRegistrationCode(),updatePasswordDto.getPassword());
    };

}
