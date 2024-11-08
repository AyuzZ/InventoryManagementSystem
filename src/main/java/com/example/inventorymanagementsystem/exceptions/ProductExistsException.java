package com.example.inventorymanagementsystem.exceptions;

public class ProductExistsException extends RuntimeException{

    public ProductExistsException(String errorMessage){
        super(errorMessage);
    }
}
