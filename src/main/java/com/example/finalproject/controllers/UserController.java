package com.example.finalproject.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @PostMapping("/register")
    public void registerUser(){
        System.out.println("Register Post");
    }

    @PostMapping("/login")
    public void loginUser(){

    }

}
