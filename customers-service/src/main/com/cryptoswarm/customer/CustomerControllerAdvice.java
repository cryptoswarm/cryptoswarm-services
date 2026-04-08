package com.cryptoswarm.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                             HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        ProblemDetailResponse problem = new ProblemDetailResponse(
                "https://example.com/problems/validation-error",
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                "One or more validation errors occurred. See the errors property for details.",
                request.getRequestURI(),
                OffsetDateTime.now().toString(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailResponse> handleInvalidRequestBody(HttpMessageNotReadableException ex,
                                                                         HttpServletRequest request) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
                "https://example.com/problems/invalid-request-body",
                "Invalid request body",
                HttpStatus.BAD_REQUEST.value(),
                "Request body is missing or malformed JSON.",
                request.getRequestURI(),
                OffsetDateTime.now().toString(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
