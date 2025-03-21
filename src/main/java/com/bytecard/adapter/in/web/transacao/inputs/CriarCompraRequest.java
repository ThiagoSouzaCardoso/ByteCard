package com.bytecard.adapter.in.web.transacao.inputs;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.Transacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CriarCompraRequest {

    @NotNull private Long cartaoId;
    @NotBlank private String cartaoNumero;
    @NotNull @DecimalMin("0.01") private BigDecimal valor;
    @NotNull  private CategoriaTransacao categoria;
    @NotBlank private String estabelecimento;


    public Transacao toModel(){

        return Transacao.builder()
                .cartao(Cartao.builder().numero(this.cartaoNumero).build())
                .categoria(this.categoria)
                .estabelecimento(this.estabelecimento)
                .valor(this.valor)
                .id(this.cartaoId)
                .build();

    }
}
