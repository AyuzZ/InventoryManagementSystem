package com.example.inventorymanagementsystem.exceptions;

public class VendorProductNotFoundException extends RuntimeException{

    public VendorProductNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
