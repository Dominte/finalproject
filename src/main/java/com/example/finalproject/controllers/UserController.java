package com.example.finalproject.controllers;

import com.example.finalproject.dtos.*;
import com.example.finalproject.services.UserService;
import com.example.finalproject.services.UserServiceSecurity;
import com.example.finalproject.utils.JwtTokenUtil;
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

    private UserServiceSecurity userServiceSecurity;

    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(JwtTokenUtil jwtTokenUtil,UserService userService,UserServiceSecurity userServiceSecurity) {
        this.userServiceSecurity = userServiceSecurity;
        this.userService = userService;
        this.jwtTokenUtil=jwtTokenUtil;
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

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestBody LoginDto loginDto){

        final String Token = jwtTokenUtil.generateToken(userServiceSecurity.loadUserByUsername(loginDto.getUsername()));
        return new ResponseEntity<>(new TokenDto(Token),HttpStatus.OK);
    }

}
