package com.example.inventorymanagementsystem.exceptions;

public class PurchaseOrderExistsException extends RuntimeException{

    public PurchaseOrderExistsException(String errorMessage){
        super(errorMessage);
    }
}
