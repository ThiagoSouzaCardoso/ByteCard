package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.Transacao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CompraItemResponse(
        OffsetDateTime data,
        String estabelecimento,
        CategoriaTransacao categoria,
        BigDecimal valor
) {

    public static CompraItemResponse from(Transacao t) {
        return new CompraItemResponse(
                t.getData(),
                t.getEstabelecimento(),
                t.getCategoria(),
                t.getValor()
        );
    }
}
