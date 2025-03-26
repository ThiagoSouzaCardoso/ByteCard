package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.Transacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(name = "CompraItemResponse", description = "Detalhe de uma compra realizada no cartão")
public record CompraItemResponse(
        @Schema(description = "Data da compra", example = "2024-12-10T14:35:00Z")
        OffsetDateTime data,

        @Schema(description = "Nome do estabelecimento", example = "Supermercado Pão de Açúcar")
        String estabelecimento,

        @Schema(description = "Categoria da transação", example = "ALIMENTACAO")
        CategoriaTransacao categoria,

        @Schema(description = "Valor da compra", example = "199.99")
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
