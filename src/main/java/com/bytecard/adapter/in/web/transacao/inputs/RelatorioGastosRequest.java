package com.bytecard.adapter.in.web.transacao.inputs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record RelatorioGastosRequest(
        @NotBlank String numeroCartao,
        @NotNull YearMonth mesAno
) {}
