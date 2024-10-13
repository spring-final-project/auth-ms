package com.springcloud.demo.authms.exceptions;

import com.springcloud.demo.authms.exceptions.dto.ErrorResponseDTO;
import com.springcloud.demo.authms.monitoring.TracingExceptions;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Hidden
@RequiredArgsConstructor
public class HandlerExceptions {

    private final TracingExceptions tracingExceptions;

    @ExceptionHandler(InheritedException.class)
    public ResponseEntity<ErrorResponseDTO> handleSimpleException(InheritedException e) {
        tracingExceptions.addExceptionMetadata(e.getMessage());

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(e.getStatus())
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleForbiddenExceptions(ForbiddenException e){
        tracingExceptions.addExceptionMetadata(e.getMessage());

        return ErrorResponseDTO
                .builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleBadRequestException(BadRequestException e){
        tracingExceptions.addExceptionMetadata(e.getMessage());

        return ErrorResponseDTO
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    /**
     * Handle errors when fail any validation in body request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<String> errors = e.getFieldErrors().stream().map(err -> err.getField() + " " + err.getDefaultMessage()).toList();

        tracingExceptions.addExceptionMetadata(e.getMessage());

        return ErrorResponseDTO
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .build();
    }

    /**
     * Handle errors when not exist body request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        String message = e.getMessage().split(":")[0];

        tracingExceptions.addExceptionMetadata(e.getMessage());

        return ErrorResponseDTO
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }
}
