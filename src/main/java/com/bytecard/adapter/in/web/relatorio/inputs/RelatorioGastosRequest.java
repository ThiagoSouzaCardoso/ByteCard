package com.bytecard.adapter.in.web.relatorio.inputs;

import com.bytecard.domain.model.CriarRelatorio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.YearMonth;

@Schema(name = "RelatorioGastosRequest", description = "Parâmetros para gerar relatório de gastos por categoria")
public record RelatorioGastosRequest(

        @Schema(description = "Número do cartão", example = "1234567812345678")
        @NotBlank String numeroCartao,

        @Schema(description = "Mês/ano no formato yyyy-MM", example = "2024-12")
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM")
        YearMonth mesAno
) {
    public CriarRelatorio toModel() {
        return new CriarRelatorio(numeroCartao, mesAno);
    }

}
