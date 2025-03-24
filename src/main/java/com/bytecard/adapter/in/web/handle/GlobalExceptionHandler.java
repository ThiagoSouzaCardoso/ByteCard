package com.bytecard.adapter.in.web.handle;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.exception.LimiteExcedidoException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private Map<String, Object> buildErrorResponse(HttpStatus status, String message, String errorLabel) {
        return Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", status.value(),
                "error", errorLabel,
                "message", message
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "Usuário não Autorizado"));
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNotFound(ClienteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "Cliente não encontrado"));
    }

    @ExceptionHandler(CartaoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartaoNotFound(CartaoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "Cartão não encontrado"));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "Conflito de dados"));
    }

    @ExceptionHandler(RelatorioEmptyException.class)
    public ResponseEntity<Map<String, Object>> handleRelatorioEmpty(RelatorioEmptyException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "Relatório vazio"));
    }

    @ExceptionHandler(CartaoBloqueadoException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(CartaoBloqueadoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "Cartão indisponível"));
    }

    @ExceptionHandler(CartaoCanceladoException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(CartaoCanceladoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "Cartão indisponível"));
    }

    @ExceptionHandler(LimiteExcedidoException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(LimiteExcedidoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "Cartão não possui limite"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Credenciais Invalidas"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

}

