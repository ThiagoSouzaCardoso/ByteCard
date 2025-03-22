package com.bytecard.adapter.in.web.cartao.inputs;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarCartaoRequest(

        @NotNull(message = "Limite é obrigatório")
        @DecimalMin(value = "0.01", message = "Limite deve ser um valor positivo")
        BigDecimal limite,

        @NotEmpty(message = "Cliente é obrigatório")
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
