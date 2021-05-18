package com.example.finalproject.dtos;

import com.example.finalproject.utils.UserRole;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRoleDto {

    @NotNull
    private UserRole role;

    private String registrationCode;

}
