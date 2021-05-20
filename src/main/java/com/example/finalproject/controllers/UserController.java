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


    @Autowired
    public UserController(UserService userService, UserServiceSecurity userServiceSecurity) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<?> registerUser(@RequestBody @Validated RegisterDto registerDto) {
        return userService.registerUser(registerDto);
    }

    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<ResponseDto> deleteUser(@RequestParam Long userId, @RequestHeader(name = "Authorization") String token) {
        return userService.deleteUser(userId, token);
    }

    @GetMapping("")
    @SneakyThrows
    public ResponseEntity<?> getUser(@RequestParam Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/update/password")
    public ResponseEntity<ResponseDto> modifyPassword(@RequestBody @Validated UpdatePasswordDto updatePasswordDto, @RequestHeader(name = "Authorization") String token) {
        return userService.modifyPassword(updatePasswordDto, token);
    }

    @PutMapping("/update/role")
    public ResponseEntity<ResponseDto> modifyRole(@RequestBody @Validated UpdateRoleDto updateRoleDto, @RequestHeader(name = "Authorization") String token) {
        return userService.modifyRole(updateRoleDto,token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        return userService.loginUser(loginDto);
    }


}
