package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/autorizacoes")
@Tag(name = "Autorização", description = "Autenticação de usuários e geração de token JWT")
public interface AutorizacaoControllerSwagger {

    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário e retorna um token JWT válido.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Credenciais do usuário",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                      "username": "usuario@usuario.com",
                      "password": "senhaSegura123"
                    }
                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token JWT retornado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas"
                    )
            }
    )
    TokenResponse login(LoginRequest loginRequest);

}

