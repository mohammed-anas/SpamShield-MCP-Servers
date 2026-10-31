package com.spamshield.rest.callrecord;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    ResponseEntity<Map<String, Object>> invalid(WebExchangeBindException exception) {
        var errors = exception.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    @ExceptionHandler(ServerWebInputException.class)
    ResponseEntity<Map<String, String>> unreadable(ServerWebInputException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", "Request body is invalid"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> status(ResponseStatusException exception) {
        String message = exception.getReason() == null ? exception.getStatusCode().toString() : exception.getReason();
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("error", message));
    }
}
