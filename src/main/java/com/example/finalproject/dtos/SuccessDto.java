package com.example.finalproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class SuccessDto {

    private final Integer statusCode = HttpStatus.OK.value();

    private final String statusMessage = HttpStatus.OK.getReasonPhrase();

    public String customMessage = "";

    public Integer getStatusCode() {
        return statusCode;
    }
    public SuccessDto(String customMessage){
        this.customMessage=customMessage;
    }

    public SuccessDto() {
    }
}
