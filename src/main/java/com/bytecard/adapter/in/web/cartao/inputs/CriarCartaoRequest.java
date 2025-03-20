package com.bytecard.adapter.in.web.cartao.inputs;

import java.math.BigDecimal;

public record CriarCartaoRequest(
        BigDecimal limite,
        String  email
) {
}
