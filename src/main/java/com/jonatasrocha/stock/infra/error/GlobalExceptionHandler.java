package com.jonatasrocha.stock.infra.error;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Response> validationHandler(MethodArgumentNotValidException e) {
        Map<String, List<String>> errorDetail = e.getBindingResult()
        .getAllErrors()
        .stream()
        .filter(error -> error instanceof FieldError)
        .map(error -> (FieldError) error)
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(
                FieldError::getDefaultMessage,
                Collectors.toList()
            )
        ));

        return responseFail(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "CONTRACT_VIOLATION",
            "Request validation failed",
            errorDetail
        );
    } 

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Response> genericHandler(Exception e) {

        log.error("An unexpected error occurred", e);

        return responseFail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "UNEXPECTED_ERROR",
            "An unexpected error occurred"
        );
    } 

}
