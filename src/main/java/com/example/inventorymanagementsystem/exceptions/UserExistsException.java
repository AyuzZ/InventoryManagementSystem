package com.example.inventorymanagementsystem.exceptions;

public class UserExistsException extends RuntimeException{

    public UserExistsException(String errorMessage){
        super(errorMessage);
    }
}
