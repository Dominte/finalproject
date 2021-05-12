package com.example.finalproject.dtos;

import com.example.finalproject.models.Question;
import com.example.finalproject.models.User;

import javax.persistence.OneToOne;

public class SubmitAnswerDto {
    private String text;

    private int questionIndex;

    private String studentRegistrationCode;
}
