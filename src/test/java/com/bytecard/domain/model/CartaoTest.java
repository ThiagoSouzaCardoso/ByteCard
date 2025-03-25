package com.bytecard.domain.model;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.LimiteExcedidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartaoTest {

    @Test
    void deveCalcularLimiteDisponivelCorretamente() {
        Cartao cartao = Cartao.builder()
                .limite(new BigDecimal("1000.00"))
                .limiteUtilizado(new BigDecimal("200.00"))
                .build();

        assertThat(cartao.getLimiteDisponivel()).isEqualByComparingTo("800.00");
    }

    @Test
    void deveLancarExcecaoSeCartaoBloqueado() {
        Cartao cartao = Cartao.builder()
                .status(StatusCartao.BLOQUEADO)
                .build();

        assertThatThrownBy(cartao::verificarStatusCartao)
                .isInstanceOf(CartaoBloqueadoException.class);
    }

    @Test
    void deveLancarExcecaoSeCartaoCancelado() {
        Cartao cartao = Cartao.builder()
                .status(StatusCartao.CANCELADO)
                .build();

        assertThatThrownBy(cartao::verificarStatusCartao)
                .isInstanceOf(CartaoCanceladoException.class);
    }

    @Test
    void deveLancarExcecaoSeLimiteInsuficiente() {
        Cartao cartao = Cartao.builder()
                .limite(new BigDecimal("500.00"))
                .limiteUtilizado(new BigDecimal("480.00"))
                .build();

        assertThatThrownBy(() -> cartao.verificarLimite(new BigDecimal("30.00")))
                .isInstanceOf(LimiteExcedidoException.class);
    }

    @Test
    void deveRegistrarCompraComSucesso() {
        Cartao cartao = Cartao.builder()
                .limite(new BigDecimal("1000.00"))
                .limiteUtilizado(new BigDecimal("100.00"))
                .status(StatusCartao.ATIVO)
                .build();

        cartao.registrarCompra(new BigDecimal("200.00"));

        assertThat(cartao.getLimiteUtilizado()).isEqualByComparingTo("300.00");
    }

    @Test
    void deveCalcularLimiteDisponivelComLimiteZero() {
        Cartao cartao = Cartao.builder()
                .limite(BigDecimal.ZERO)
                .limiteUtilizado(BigDecimal.ZERO)
                .build();

        assertThat(cartao.getLimiteDisponivel()).isEqualByComparingTo("0.00");
    }

    @Test
    void deveCalcularLimiteDisponivelComValoresNegativos() {
        Cartao cartao = Cartao.builder()
                .limite(new BigDecimal("-100.00"))
                .limiteUtilizado(new BigDecimal("-50.00"))
                .build();

        assertThat(cartao.getLimiteDisponivel()).isEqualByComparingTo("-50.00");
    }

    @Test
    void deveCalcularLimiteDisponivelComValoresNulos() {
        Cartao cartao = Cartao.builder()
                .limite(null)
                .limiteUtilizado(null)
                .build();

        assertThatThrownBy(cartao::getLimiteDisponivel)
                .isInstanceOf(NullPointerException.class);
    }
}
