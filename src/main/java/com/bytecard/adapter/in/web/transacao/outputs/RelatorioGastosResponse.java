package com.bytecard.adapter.in.web.transacao.outputs;

import com.bytecard.domain.model.GastoCategoria;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioGastosResponse(
        String cartaoNumero,
        String mes,
        List<GastoCategoria> gastos,
        BigDecimal valorTotalGasto
) {}
