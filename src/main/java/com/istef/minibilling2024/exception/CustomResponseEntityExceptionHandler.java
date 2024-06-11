package com.istef.minibilling2024.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        if (ex.getCause() != null && ex.getCause() instanceof NoSuchMethodError) {
            return response("Invalid numeric value: String", request, HttpStatus.BAD_REQUEST);
        }

        return response(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {

        return response(ex.getCause().getCause().getMessage(), request, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(Exception ex, WebRequest request) {

        return response(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @Nullable HttpHeaders headers,
            @Nullable HttpStatus status,
            @Nullable WebRequest request) {

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < ex.getErrorCount(); i++) {
            stringBuilder.append(ex.getFieldErrors().get(i).getDefaultMessage());
            if (i < ex.getErrorCount() - 1) stringBuilder.append(", ");
        }

        return response(stringBuilder.toString(), request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> response(String message,
                                            @Nullable WebRequest request,
                                            HttpStatus status) {
        if (request == null) return ResponseEntity.badRequest().build();
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                message,
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, status);
    }
}
