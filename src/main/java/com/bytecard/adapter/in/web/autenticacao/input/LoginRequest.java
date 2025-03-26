package com.bytecard.adapter.in.web.autenticacao.input;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Parameter(description = "Usuário (email ou login)", required = true, example = "usuario@usuario.com")
        @NotBlank(message = "O campo 'username' é obrigatório")
        @Email(message = "Formato de email inválido")
        String username,

        @Parameter(description = "Senha do usuário", required = true, example = "senhaUsuario")
        @NotBlank(message = "O campo 'password' é obrigatório") String password
) {}
