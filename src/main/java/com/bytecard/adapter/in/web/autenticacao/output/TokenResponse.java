package com.bytecard.adapter.in.web.autenticacao.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenResponse", description = "Token JWT gerado após autenticação")
public record TokenResponse(

        @Schema(description = "Token JWT válido para autenticação nas demais requisições", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
        String token
) {}