package com.example.Commerce.errorhandlers;
import io.swagger.v3.oas.annotations.Hidden;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Hidden
public class CustomExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put(("path"), request.getDescription(false));
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceAlreadyExists.class)
    public ResponseEntity<?> handleEmailAlreadyExistsException(ResourceAlreadyExists ex, WebRequest request) {
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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        // Extract only the field names and their corresponding error messages
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                            (existing, replacement) -> existing
                ));

        HashMap<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("message", "Required request body is missing or malformed");
        error.put("path", request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = JDBCException.class)
    public ResponseEntity<?> handleJDBCException(JDBCException ex, WebRequest request) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", new Date());
        error.put("message", "Database error: " + ex.getMessage());
        error.put("path", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
