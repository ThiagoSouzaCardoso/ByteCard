package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autorização", description = "Autenticação de usuários e geração de token JWT")
public interface AutorizacaoControllerSwagger {

    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário com login e senha e retorna um token JWT válido para ser usado nas demais requisições.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Credenciais do usuário para login",
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login realizado com sucesso. Token JWT gerado.",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas. Usuário ou senha incorretos."
                    )
            }
    )
    TokenResponse login(LoginRequest loginRequest);
}

