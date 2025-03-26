package com.bytecard.adapter.in.web.handle;

import com.bytecard.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("HTTP 404 - NOT FOUND")
    class NotFoundTests {

        @Test
        @DisplayName("UsernameNotFoundException")
        void deveRetornarNotFoundParaUsernameNotFoundException() {
            var response = handler.handleUsernameNotFoundException(new UsernameNotFoundException("Usuário não encontrado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsEntry("message", "Usuário não encontrado");
        }

        @Test
        @DisplayName("ClienteNotFoundException")
        void deveRetornarNotFoundParaClienteNotFoundException() {
            var response = handler.handleClienteNotFound(new ClienteNotFoundException("Cliente não encontrado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsEntry("message", "Cliente não encontrado");
        }

        @Test
        @DisplayName("CartaoNotFoundException")
        void deveRetornarNotFoundParaCartaoNotFoundException() {
            var response = handler.handleCartaoNotFound(new CartaoNotFoundException("Cartão não encontrado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsEntry("message", "Cartão não encontrado");
        }
    }

    @Nested
    @DisplayName("HTTP 409 - CONFLICT")
    class ConflictTests {

        @Test
        @DisplayName("UserAlreadyExistException")
        void deveRetornarConflictParaUserAlreadyExistException() {
            var response = handler.handleUserAlreadyExists(new UserAlreadyExistException("Já existe"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).containsEntry("message", "Já existe");
        }
    }

    @Nested
    @DisplayName("HTTP 422 - UNPROCESSABLE ENTITY")
    class UnprocessableEntityTests {

        @Test
        @DisplayName("RelatorioEmptyException")
        void deveRetornarUnprocessableEntityParaRelatorioEmptyException() {
            var response = handler.handleRelatorioEmpty(new RelatorioEmptyException("Nada encontrado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).containsEntry("message", "Nada encontrado");
        }

        @Test
        @DisplayName("CartaoBloqueadoException")
        void deveRetornarUnprocessableEntityParaCartaoBloqueadoException() {
            var response = handler.handleForbidden(new CartaoBloqueadoException("Cartão bloqueado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).containsEntry("message", "Cartão bloqueado");
        }

        @Test
        @DisplayName("CartaoCanceladoException")
        void deveRetornarUnprocessableEntityParaCartaoCanceladoException() {
            var response = handler.handleForbidden(new CartaoCanceladoException("Cartão cancelado"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).containsEntry("message", "Cartão cancelado");
        }

        @Test
        @DisplayName("LimiteExcedidoException")
        void deveRetornarUnprocessableEntityParaLimiteExcedidoException() {
            var response = handler.handleForbidden(new LimiteExcedidoException("Limite excedido"));
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody()).containsEntry("message", "Limite excedido");
        }
    }

    @Nested
    @DisplayName("HTTP 401 - UNAUTHORIZED")
    class UnauthorizedTests {

        @Test
        @DisplayName("AuthenticationException")
        void deveRetornarUnauthorizedParaAuthenticationException() {
            var response = handler.handleForbidden(new AuthenticationException("Token inválido") {});
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).containsEntry("message", "Token inválido");
        }
    }

    @Nested
    @DisplayName("HTTP 400 - BAD REQUEST")
    class BadRequestTests {

        @Test
        @DisplayName("Erros de validação com MethodArgumentNotValidException")
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

        @Test
        @DisplayName("Parâmetro com formato inválido")
        void deveRetornarBadRequestQuandoParametroPossuiFormatoInvalido() {
            MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
            when(ex.getName()).thenReturn("mesAno");

            ResponseEntity<Object> response = handler.handleTypeMismatch(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(Map.class);

            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) response.getBody();

            assertThat(body).containsKey("erro");
            assertThat(body.get("erro")).contains("mesAno");
            assertThat(body.get("erro")).contains("yyyy-MM");
        }

        @Test
        @DisplayName("Formato inválido para YearMonth")
        void deveTratarFormatoInvalidoParaYearMonth() {
            String mensagemSimulada = "JSON parse error: Cannot deserialize value of type `java.time.YearMonth` from String \"2025/03\"";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException(mensagemSimulada, (Throwable) null, null);

            ResponseEntity<Map<String, String>> response = handler.handleInvalidFormat(exception);

            assertThat(response.getStatusCode().value()).isEqualTo(400);
            assertThat(response.getBody()).containsEntry("erro", "Formato inválido para data. Esperado: yyyy-MM (ex: 2025-03)");
        }

        @Test
        @DisplayName("Outros erros de leitura do corpo da requisição")
        void deveTratarOutrosErrosDeLeituraDoCorpo() {
            String mensagemSimulada = "Erro de leitura genérico";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException(mensagemSimulada, (Throwable) null, null);

            ResponseEntity<Map<String, String>> response = handler.handleInvalidFormat(exception);

            assertThat(response.getStatusCode().value()).isEqualTo(400);
            assertThat(response.getBody()).containsEntry("erro", "Formato inválido no corpo da requisição. Verifique os campos e tente novamente.");
        }

        @Test
        @DisplayName("Deve retornar mensagem padrão quando ex.getMessage() é null")
        void deveTratarMensagemNulaNaExcecao() {
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
            when(exception.getMessage()).thenReturn(null);

            ResponseEntity<Map<String, String>> response = handler.handleInvalidFormat(exception);

            assertThat(response.getStatusCode().value()).isEqualTo(400);
            assertThat(response.getBody()).containsEntry("erro", "Formato inválido no corpo da requisição. Verifique os campos e tente novamente.");
        }

    }
}