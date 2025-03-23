package com.bytecard.domain.model;

import java.time.YearMonth;

public record CriarRelatorio(

        String numeroCartao,
        YearMonth mesAno
) {
}
