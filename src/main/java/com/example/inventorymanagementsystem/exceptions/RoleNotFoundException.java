package com.example.inventorymanagementsystem.exceptions;

public class RoleNotFoundException extends RuntimeException{

    public RoleNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
