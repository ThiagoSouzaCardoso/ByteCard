package com.bytecard.adapter.in.web.autenticacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Autorização", description = "Autenticação de usuários e geração de token JWT")
public interface AutorizacaoControllerSwagger {

    @Operation(
            summary = "Login de usuário",
            description = "Autentica o usuário com login e senha e retorna um token JWT válido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    String login(
            @Parameter(description = "Usuário (email ou login)", required = true, example = "usuario@usuario.com")
            @RequestParam String username,

            @Parameter(description = "Senha do usuário", required = true, example = "senhaUsuario")
            @RequestParam String password
    );
}

