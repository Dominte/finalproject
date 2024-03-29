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

    private String title;

    private String testDate;

    private String startingHour;

    private String finishingHour;

}
