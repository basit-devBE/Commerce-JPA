package com.example.Commerce.errorHandlers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put(("path"), request.getDescription(false));
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EmailAlreadyExists.class)
    public ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExists ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put(("path"), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request){
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put(("path"), request.getDescription(false));
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
