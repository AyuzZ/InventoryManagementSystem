package com.example.inventorymanagementsystem.exceptions;

public class VendorExistsException extends RuntimeException{
    public VendorExistsException(String errorMessage){
        super(errorMessage);
    }
}
