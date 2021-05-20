package com.example.finalproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatedTestDto {

    private String title;

    private LocalDate testDate;

    private LocalTime finishingHour;

    private LocalTime startingHour;

}
