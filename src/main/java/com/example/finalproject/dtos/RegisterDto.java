package com.example.finalproject.dtos;


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

}
