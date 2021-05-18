package com.example.finalproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateTestDto {

    private Long testId;

    private String newTitle;

    private String newTestDay;

    private String newStartingHour;

    private String newDuration;
}
