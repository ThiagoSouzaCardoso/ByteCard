package com.bytecard.adapter.in.web.relatorio.inputs;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.Transacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CriarCompraRequest {

    @NotBlank(message = "Numero do Cartão é obrigatório")
    private String cartaoNumero;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "Categoria é obrigatória")
    private CategoriaTransacao categoria;

    @NotBlank(message = "Estabelecimento é obrigatório")
    @Size(min = 5, message = "Estabelecimento deve ter no mínimo 5 caracteres")
    private String estabelecimento;


    public Transacao toModel(){

        return Transacao.builder()
                .cartao(Cartao.builder().numero(this.cartaoNumero).build())
                .categoria(this.categoria)
                .estabelecimento(this.estabelecimento)
                .valor(this.valor)
                .build();

    }
}
