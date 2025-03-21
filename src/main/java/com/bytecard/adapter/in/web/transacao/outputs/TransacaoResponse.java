package com.bytecard.adapter.in.web.transacao.outputs;

import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.domain.model.CategoriaTransacao;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
@Relation(collectionRelation = "transacoes")
public class TransacaoResponse extends RepresentationModel<CartaoResponse> {

    private Long id;
    private String cartaoNumero;
    private BigDecimal valor;
    private CategoriaTransacao categoria;
    private String estabelecimento;
    private OffsetDateTime dataHora;

}
