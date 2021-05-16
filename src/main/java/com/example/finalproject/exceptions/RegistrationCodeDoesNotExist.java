package com.example.finalproject.exceptions;

public class RegistrationCodeDoesNotExist extends Exception{
    public RegistrationCodeDoesNotExist(String errorMessage){
        super(errorMessage);
    }
}
