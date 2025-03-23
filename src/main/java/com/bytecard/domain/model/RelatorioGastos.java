package com.bytecard.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioGastos(
        String cartaoNumero,
        String mes,
        List<GastoCategoria> gastos,
        BigDecimal valorTotalGasto
) {
}
