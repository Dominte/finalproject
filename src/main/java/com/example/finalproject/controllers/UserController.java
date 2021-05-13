package com.example.finalproject.controllers;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.ResponseDto;
import com.example.finalproject.dtos.SuccessDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> modifyPassword(@RequestBody @Validated UpdatePasswordDto updatePasswordDto){
        return userService.modifyPassword(updatePasswordDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(){
        return new ResponseEntity<>(new ResponseDto(HttpStatus.OK, "Success"), HttpStatus.OK);
    }
}
