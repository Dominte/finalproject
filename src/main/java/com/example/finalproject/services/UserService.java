package com.example.finalproject.services;

import com.example.finalproject.dtos.*;
import com.example.finalproject.exceptions.UserAlreadyExistsException;
import com.example.finalproject.models.User;
import com.example.finalproject.repositories.UserRepository;
import com.example.finalproject.utils.GeneratePassword;
import com.example.finalproject.utils.JwtTokenUtil;
import com.example.finalproject.utils.StringHash;
import com.example.finalproject.utils.UserRole;
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
    public ResponseEntity<?> registerUser(RegisterDto registerDto) {
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
            String password = GeneratePassword.generate(20);

            User user = User.builder()
                    .firstName(registerDto.getFirstName().trim())
                    .lastName(registerDto.getLastName().trim())
                    .email(registerDto.getEmail().trim())
                    .registrationCode(registerDto.getRegistrationCode().trim())
                    .role(registerDto.getRole())
                    .username(username + increment)
                    .password(StringHash.encode(password)).build();

            LoginDto loginDto = new LoginDto();
            loginDto.setUsername(username + increment);
            loginDto.setPassword(password);

            userRepository.save(user);
            return new ResponseEntity<>(loginDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }

    @SneakyThrows
    public ResponseEntity<?> getUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "User does not exist"), HttpStatus.CONFLICT);
        }

        try {

            User user = userRepository.findById(userId).get();

            UserDto userDto = UserDto.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .registrationCode(user.getRegistrationCode())
                    .build();

            return new ResponseEntity<>(userDto, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> deleteUser(Long userId, String token) {
        token = token.substring(7);

        if (userRepository.findById(userId).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "User does not exist"), HttpStatus.CONFLICT);
        }

        if (userRepository.findById(userId).get().getRegistrationCode().equals(jwtTokenUtil.getRegistrationCodeFromToken(token))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Cannot delete your own account"), HttpStatus.CONFLICT);
        }

        try {

            User user = userRepository.findById(userId).get();
            userRepository.delete(user);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "User deleted successfully"), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }


    @SneakyThrows
    public ResponseEntity<ResponseDto> modifyPassword(UpdatePasswordDto updatePasswordDto, String token) {
        token = token.substring(7);

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

        if (!userRepository.findUserByUsername(loginDto.getUsername()).get().getPassword().equals(StringHash.encode(loginDto.getPassword()))) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Wrong username or password"), HttpStatus.CONFLICT);
        }

        try {
            final String Token = jwtTokenUtil.generateToken(userServiceSecurity.loadUserByUsername(loginDto.getUsername()));
            return new ResponseEntity<>(new TokenDto(Token), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }

    }

    @SneakyThrows
    public ResponseEntity<ResponseDto> modifyRole(UpdateRoleDto updateRoleDto, String token) {
        token = token.substring(7);

        if (userRepository.findUserByRegistrationCode(updateRoleDto.getRegistrationCode()).isEmpty()) {
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "User does not exist"), HttpStatus.CONFLICT);
        }

        if(jwtTokenUtil.getUsernameFromToken(token).equals(userRepository.findUserByRegistrationCode(updateRoleDto.getRegistrationCode()).get().getUsername())){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CONFLICT, "Cannot change your own role"), HttpStatus.CONFLICT);
        }

        for (UserRole role : UserRole.values()) {
            if (role.equals(updateRoleDto.getRole())) {
                try {
                    User user = userRepository.findUserByRegistrationCode(updateRoleDto.getRegistrationCode()).get();
                    user.setRole(updateRoleDto.getRole());

                    userRepository.save(user);
                    return new ResponseEntity<>(new ResponseDto(HttpStatus.ACCEPTED, "Role has been changed successfully"), HttpStatus.ACCEPTED);
                } catch (Exception e) {
                    return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Something went wrong"), HttpStatus.BAD_REQUEST);
                }

            }
        }
        return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Role cannot be unspecified or have another value than: ADMIN, STUDENT, TEACHER"), HttpStatus.BAD_REQUEST);
    }
}
