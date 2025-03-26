package com.bytecard.adapter.in.web.transacao.inputs;

import com.bytecard.domain.model.CriarRelatorio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

public record RelatorioGastosRequest(
        @NotBlank String numeroCartao,

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM")
        YearMonth mesAno
) {
    public CriarRelatorio toModel() {
        return new CriarRelatorio(numeroCartao, mesAno);
    }

}
