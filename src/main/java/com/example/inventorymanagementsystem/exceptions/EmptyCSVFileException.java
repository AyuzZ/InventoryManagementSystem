package com.example.inventorymanagementsystem.exceptions;

public class EmptyCSVFileException extends RuntimeException{

    public EmptyCSVFileException(String errorMessage){
        super(errorMessage);
    }
}
