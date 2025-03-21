package com.bytecard.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class Transacao {

    private Long id;
    private Cartao cartao;
    private BigDecimal valor;
    private OffsetDateTime data;
    private CategoriaTransacao categoria;
    private String estabelecimento;

}
