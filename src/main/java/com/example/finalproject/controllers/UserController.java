package com.example.finalproject.controllers;

import com.example.finalproject.dtos.RegisterDto;
import com.example.finalproject.dtos.UpdatePasswordDto;
import com.example.finalproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Validated RegisterDto registerDto){
        System.out.println("Register user");
        userService.registerUser(registerDto);
    }

    @PostMapping("/update")
    public void loginUser(@RequestBody @Validated UpdatePasswordDto updatePasswordDto){
        System.out.println("Update password");
        userService.modifyPassword(updatePasswordDto);
    }

}
