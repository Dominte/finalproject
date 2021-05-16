package com.example.finalproject.dtos;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class TokenDto {
    private final Integer statusCode = HttpStatus.OK.value();

    private final String statusMessage = HttpStatus.OK.getReasonPhrase();

    private String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
