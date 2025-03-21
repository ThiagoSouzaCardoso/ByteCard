package com.bytecard.adapter.in.web.cartao.outputs;


import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.domain.model.StatusCartao;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Builder
@Getter
@Relation(collectionRelation = "cartoes")
public class CartaoResponse extends RepresentationModel<CartaoResponse> {

    private Long id;
    private String numero;
    private String cliente;
    private BigDecimal limite;
    private String validade;
    private StatusCartao status;

}

