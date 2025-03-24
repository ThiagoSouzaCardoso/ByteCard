package com.bytecard.adapter.in.web.handle;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.exception.LimiteExcedidoException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.exception.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void deveRetornarNotFoundParaUsernameNotFoundException() {
        var response = handler.handleUsernameNotFoundException(new UsernameNotFoundException("Usuário não encontrado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Usuário não encontrado");
    }

    @Test
    void deveRetornarNotFoundParaClienteNotFoundException() {
        var response = handler.handleClienteNotFound(new ClienteNotFoundException("Cliente não encontrado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Cliente não encontrado");
    }

    @Test
    void deveRetornarNotFoundParaCartaoNotFoundException() {
        var response = handler.handleCartaoNotFound(new CartaoNotFoundException("Cartão não encontrado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Cartão não encontrado");
    }

    @Test
    void deveRetornarConflictParaUserAlreadyExistException() {
        var response = handler.handleUserAlreadyExists(new UserAlreadyExistException("Já existe"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("message", "Já existe");
    }

    @Test
    void deveRetornarUnprocessableEntityParaRelatorioEmptyException() {
        var response = handler.handleRelatorioEmpty(new RelatorioEmptyException("Nada encontrado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("message", "Nada encontrado");
    }

    @Test
    void deveRetornarUnprocessableEntityParaCartaoBloqueadoException() {
        var response = handler.handleForbidden(new CartaoBloqueadoException("Cartão bloqueado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("message", "Cartão bloqueado");
    }

    @Test
    void deveRetornarUnprocessableEntityParaCartaoCanceladoException() {
        var response = handler.handleForbidden(new CartaoCanceladoException("Cartão cancelado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("message", "Cartão cancelado");
    }

    @Test
    void deveRetornarUnprocessableEntityParaLimiteExcedidoException() {
        var response = handler.handleForbidden(new LimiteExcedidoException("Limite excedido"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("message", "Limite excedido");
    }

    @Test
    void deveRetornarUnauthorizedParaAuthenticationException() {
        var response = handler.handleForbidden(new AuthenticationException("Token inválido") {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("message", "Token inválido");
    }

    @Test
    void deveRetornarBadRequestComMensagensDeValidacao() {
        var target = new Object();
        var bindException = new BindException(target, "target");
        bindException.addError(new FieldError("target", "email", "Email inválido"));
        bindException.addError(new FieldError("target", "senha", "Senha obrigatória"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindException.getBindingResult());

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKeys("email", "senha");
        assertThat(response.getBody()).containsEntry("email", "Email inválido");
    }
}
