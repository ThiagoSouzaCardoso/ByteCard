package com.bytecard.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cartao {
    private Long id;
    private String numero;
    private String cliente;
    private String validade;
    private String cvv;
    private Double limite;
    private String status;
}