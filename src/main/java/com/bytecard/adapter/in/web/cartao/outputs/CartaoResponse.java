package com.bytecard.adapter.in.web.cartao.outputs;


import com.bytecard.domain.model.StatusCartao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Schema(name = "CartaoResponse", description = "Informações de retorno do cartão criado ou consultado")
@Builder
@Getter
@Relation(collectionRelation = "cartoes")
public class CartaoResponse extends RepresentationModel<CartaoResponse> {

    @Schema(description = "ID do cartão", example = "1")
    private Long id;

    @Schema(description = "Número do cartão", example = "1234567812345678")
    private String numero;

    @Schema(description = "Nome do cliente", example = "João da Silva")
    private String cliente;

    @Schema(description = "Limite de crédito disponível", example = "2500.00")
    private BigDecimal limite;

    @Schema(description = "Data de validade do cartão (MM/yyyy)", example = "12/2030")
    private String validade;

    @Schema(description = "Status atual do cartão", example = "ATIVO")
    private StatusCartao status;

}

