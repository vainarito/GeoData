package com.example.geodata.exceptions;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundException(
            final ResourceNotFoundException ex, final WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> badRequestException(
            final BadRequestException ex, final WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalException(
            final Exception ex, final WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
