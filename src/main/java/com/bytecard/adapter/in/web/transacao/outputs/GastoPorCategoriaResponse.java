package com.bytecard.adapter.in.web.transacao.outputs;

import com.bytecard.domain.model.CategoriaTransacao;

import java.math.BigDecimal;

public record GastoPorCategoriaResponse(
        CategoriaTransacao categoria,
        BigDecimal total
) {}