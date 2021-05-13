package com.example.finalproject.dtos;


import com.example.finalproject.utils.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.sun.istack.NotNull;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDto {

    @NotNull
    private String firstName;

    private String lastName;

    private String email;

    private String registrationCode;

    private UserRole role;

}
