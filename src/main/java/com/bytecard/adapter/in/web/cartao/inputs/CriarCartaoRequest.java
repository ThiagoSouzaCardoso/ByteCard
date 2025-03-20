package com.bytecard.adapter.in.web.cartao.inputs;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;

import java.math.BigDecimal;

public record CriarCartaoRequest(
        BigDecimal limite,
        String  email
) {

    public  Cartao toModel() {
        return Cartao.builder()
                .cliente(Cliente.builder()
                        .email(this.email())
                        .build())
                .limite(this.limite()).build();
    }
}
