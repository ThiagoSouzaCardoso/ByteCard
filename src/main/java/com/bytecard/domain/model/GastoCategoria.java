package com.bytecard.domain.model;

import java.math.BigDecimal;

public record GastoCategoria(
        CategoriaTransacao categoria,
        BigDecimal total
) {
}
