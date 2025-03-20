package com.bytecard.adapter.in.web.transacao.outputs;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransacaoResponse {

    private Long id;
    private String cartaoNumero;
    private BigDecimal valor;
    private String categoria;
    private String estabelecimento;
    private OffsetDateTime dataHora;

}
