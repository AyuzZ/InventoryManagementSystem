package com.example.inventorymanagementsystem.config;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        // Return a custom error response
        return new ResponseEntity<>(
                "Invalid Input - Wrong data type provided. \nPlease check your response body.",
                HttpStatus.BAD_REQUEST
        );
    }

    // Generic handler for all other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        // Logging the exception for debugging.
        System.err.println("Unhandled exception: " + ex.getMessage());

        // Error message to the User
        String errorMessage = "An unexpected error occurred. Please try again later.";
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

