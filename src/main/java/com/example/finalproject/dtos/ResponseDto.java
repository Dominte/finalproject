package com.example.finalproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
public class ResponseDto {
    private Integer statusCode = HttpStatus.OK.value();

    public String customMessage = "Everything went fine";

    public Integer getStatusCode() {
        return statusCode;
    }
    public ResponseDto(){

    }

    public ResponseDto(String customMessage){
        this.customMessage=customMessage;
    }

    public ResponseDto(Integer statusCode) {
        this.statusCode=statusCode;
    }

    public ResponseDto(HttpStatus status, String customMessage) {
        this.statusCode = status.value();
        this.customMessage = customMessage;
    }
}
