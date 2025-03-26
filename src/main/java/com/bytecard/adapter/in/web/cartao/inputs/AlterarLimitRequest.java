package com.bytecard.adapter.in.web.cartao.inputs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(name = "AlterarLimitRequest", description = "Dados para alteração de limite de cartão")
public record AlterarLimitRequest(

        @Schema(description = "Novo limite do cartão", example = "3000.00")
        @NotNull(message = "O novo limite é obrigatório")
        @DecimalMin(value = "0.01", message = "O limite deve ser maior que zero")
        BigDecimal novoLimite
) {}
