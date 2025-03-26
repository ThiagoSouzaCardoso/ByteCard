package com.bytecard.adapter.in.web.autenticacao.input;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(name = "LoginRequest", description = "Credenciais de login do usuário")
@Builder
public record LoginRequest(

        @Schema(description = "E-mail do usuário", example = "usuario@usuario.com")
        @NotBlank(message = "O campo 'username' é obrigatório")
        @Email(message = "Formato de email inválido")
        String username,

        @Schema(description = "Senha do usuário", example = "senhaSegura123")
        @NotBlank(message = "O campo 'password' é obrigatório") String password
) {}
