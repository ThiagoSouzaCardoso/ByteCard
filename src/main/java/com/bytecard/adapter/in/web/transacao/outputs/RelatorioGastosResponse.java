package com.bytecard.adapter.in.web.transacao.outputs;


import java.math.BigDecimal;
import java.util.List;

public record RelatorioGastosResponse(
        String cartaoNumero,
        String mes,
        List<GastoPorCategoriaResponse> gastos,
        BigDecimal valorTotalGasto
) {}
