package com.example.finalproject.controllers;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.SuccessDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.services.UserService;
import com.sun.mail.iap.Response;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<ResponseDto> registerUser(@RequestBody @Validated RegisterDto registerDto){
        return userService.registerUser(registerDto);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDto> loginUser(@RequestBody @Validated UpdatePasswordDto updatePasswordDto){
        return userService.modifyPassword(updatePasswordDto);
    }

}
