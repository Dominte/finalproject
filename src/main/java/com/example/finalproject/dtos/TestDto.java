package com.example.finalproject.dtos;

import com.example.finalproject.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestDto {

    private String teacherRegistrationCode;

    private String title;

    private String testDay;

}
