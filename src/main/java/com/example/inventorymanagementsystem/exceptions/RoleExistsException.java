package com.example.inventorymanagementsystem.exceptions;

public class RoleExistsException extends RuntimeException{
    public RoleExistsException(String errorMessage){
        super(errorMessage);
    }
}
