package com.bytecard.adapter.in.web.cartao.inputs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlterarLimitRequest(

        @NotNull(message = "O novo limite é obrigatório")
        @DecimalMin(value = "0.01", message = "O limite deve ser maior que zero")
        BigDecimal novoLimite

) {
}
