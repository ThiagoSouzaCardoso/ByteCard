package com.bytecard.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cartao {
    private Long id;
    private String numero;
    private Cliente cliente;
    private YearMonth validade;
    private String cvv;
    private BigDecimal limite;
    private StatusCartao status;
}