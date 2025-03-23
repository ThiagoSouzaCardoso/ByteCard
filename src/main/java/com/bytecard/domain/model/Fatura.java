package com.bytecard.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record Fatura(
        Cliente cliente,
        Cartao cartao,
        YearMonth mes,
        BigDecimal valorTotal,
        List<Transacao> compras

) {
}
