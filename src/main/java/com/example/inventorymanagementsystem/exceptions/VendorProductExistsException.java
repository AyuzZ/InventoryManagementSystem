package com.example.inventorymanagementsystem.exceptions;

public class VendorProductExistsException extends RuntimeException{

    public VendorProductExistsException(String errorMessage){
        super(errorMessage);
    }
}
