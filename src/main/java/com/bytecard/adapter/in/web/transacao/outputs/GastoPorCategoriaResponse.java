package com.bytecard.adapter.in.web.transacao.outputs;

import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.GastoCategoria;

import java.math.BigDecimal;

public record GastoPorCategoriaResponse(
        CategoriaTransacao categoria,
        BigDecimal total
) {

    public static GastoPorCategoriaResponse from(GastoCategoria model) {
        return new GastoPorCategoriaResponse(model.categoria(), model.total());
    }

}