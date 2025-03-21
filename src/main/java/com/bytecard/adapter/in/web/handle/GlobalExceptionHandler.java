package com.bytecard.adapter.in.web.handle;

import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> response = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Usuário não Autorizado",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(ClienteNotFoundException ex) {
        Map<String, Object> response = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Usuário não Autorizado",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CartaoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(CartaoNotFoundException ex) {
        Map<String, Object> response = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Usuário não Autorizado",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UserAlreadyExistException ex) {
        Map<String, Object> response = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Usuário não Autorizado",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }





}

