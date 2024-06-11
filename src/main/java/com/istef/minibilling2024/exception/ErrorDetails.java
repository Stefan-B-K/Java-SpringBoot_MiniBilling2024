package com.istef.minibilling2024.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Optional;

public class ErrorDetails {
    private final LocalDateTime timestamp;
    private final String message;
    private final String details;

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }


    public static ResponseEntity<Object> fieldNotValidResponse(BindingResult bindingResults) {
        StringBuilder sb = new StringBuilder();
        Optional<FieldError> field = bindingResults.getFieldErrors().stream().findFirst();
        field.ifPresent(fieldError -> sb
                .append(fieldError.getField())
                .append(" : ")
                .append(fieldError.getDefaultMessage()));

        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), sb.toString(), null);

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
