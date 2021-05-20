package com.example.finalproject.dtos;

import com.example.finalproject.models.Question;
import com.example.finalproject.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToOne;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubmitAnswerDto {

    private String text;

    private int questionIndex;

    private Long testId;

}
