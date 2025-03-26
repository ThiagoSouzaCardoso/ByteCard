package com.bytecard.adapter.in.web.cartao.inputs;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(name = "CriarCartaoRequest", description = "Dados para criação de um novo cartão")
public record CriarCartaoRequest(

        @Schema(description = "Limite inicial do cartão", example = "2500.00")
        @NotNull(message = "Limite é obrigatório")
        @DecimalMin(value = "0.01", message = "Limite deve ser um valor positivo")
        BigDecimal limite,

        @Schema(description = "E-mail do cliente", example = "cliente@exemplo.com")
        @NotEmpty(message = "Cliente é obrigatório")
        @Email(message = "Formato de email inválido")
        String email
) {

    public  Cartao toModel() {
        return Cartao.builder()
                .cliente(Cliente.builder()
                        .email(this.email())
                        .build())
                .limite(this.limite()).build();
    }
}
