package com.example.inventorymanagementsystem.exceptions;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
