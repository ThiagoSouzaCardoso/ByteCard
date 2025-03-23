package com.bytecard.domain.model;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.LimiteExcedidoException;
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
    private BigDecimal limiteUtilizado;
    private StatusCartao status;


    public void verificarStatusCartao() {

        if (StatusCartao.BLOQUEADO.equals(status)) {
            throw new CartaoBloqueadoException("Cartão está " + status.name().toLowerCase());
        }
        if (StatusCartao.CANCELADO.equals(status)) {
            throw new CartaoCanceladoException("Cartão está " + status.name().toLowerCase());
        }

    }

    public void verificarLimite(BigDecimal valor) {
        if (getLimiteDisponivel().compareTo(valor) < 0) {
            throw new LimiteExcedidoException("Limite insuficiente para realizar a compra.");
        }
    }

    public void registrarCompra(BigDecimal valor) {
        verificarStatusCartao();
        verificarLimite(valor);
        this.limiteUtilizado = this.limiteUtilizado.add(valor);
    }

    public BigDecimal getLimiteDisponivel() {
        return limite.subtract(limiteUtilizado);
    }

}