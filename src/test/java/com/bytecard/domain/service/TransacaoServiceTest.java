package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.LimiteExcedidoException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.bytecard.domain.model.CategoriaTransacao.ALIMENTACAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransacaoServiceTest {

    private BuscaCartaoPort buscaCartaoPort;
    private RegistraCartaoPort registraCartaoPort;
    private RegistrarTransacaoPort registrarTransacaoPort;
    private TransacaoService service;

    @BeforeEach
    void setUp() {
        buscaCartaoPort = mock(BuscaCartaoPort.class);
        registraCartaoPort = mock(RegistraCartaoPort.class);
        registrarTransacaoPort = mock(RegistrarTransacaoPort.class);
        service = new TransacaoService(buscaCartaoPort, registraCartaoPort, registrarTransacaoPort);
    }

    @Test
    void deveRegistrarTransacaoComSucesso() {
        // Arrange
        Cartao cartao = Cartao.builder()
                .numero("1234567890123456")
                .limite(BigDecimal.valueOf(1000))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.ATIVO)
                .build();

        Transacao transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(200))
                .categoria(ALIMENTACAO)
                .estabelecimento("Restaurante")
                .cartao(cartao)
                .build();

        when(buscaCartaoPort.findByNumero("1234567890123456")).thenReturn(Optional.of(cartao));
        when(registraCartaoPort.save(any())).thenReturn(cartao);
        when(registrarTransacaoPort.registrar(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Transacao resultado = service.registrarCompra(transacao);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualByComparingTo("200");
        assertThat(resultado.getCartao().getLimiteUtilizado()).isEqualByComparingTo("200");

        verify(buscaCartaoPort).findByNumero("1234567890123456");
        verify(registraCartaoPort).save(cartao);
        verify(registrarTransacaoPort).registrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoNaoEncontrado() {
        Transacao transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(100))
                .cartao(Cartao.builder().numero("0000111122223333").build())
                .build();

        when(buscaCartaoPort.findByNumero("0000111122223333")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarCompra(transacao))
                .isInstanceOf(CartaoNotFoundException.class)
                .hasMessage("Cartão não encontrado");

        verify(registraCartaoPort, never()).save(any());
        verify(registrarTransacaoPort, never()).registrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoEstiverBloqueado() {
        Cartao cartao = Cartao.builder()
                .numero("1234")
                .limite(BigDecimal.valueOf(500))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.BLOQUEADO)
                .build();

        Transacao transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(100))
                .cartao(cartao)
                .build();

        when(buscaCartaoPort.findByNumero("1234")).thenReturn(Optional.of(cartao));

        assertThatThrownBy(() -> service.registrarCompra(transacao))
                .isInstanceOf(CartaoBloqueadoException.class)
                .hasMessageContaining("Cartão está bloqueado");

        verify(registraCartaoPort, never()).save(any());
        verify(registrarTransacaoPort, never()).registrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoEstiverCancelado() {
        Cartao cartao = Cartao.builder()
                .numero("1234")
                .limite(BigDecimal.valueOf(500))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.CANCELADO)
                .build();

        Transacao transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(100))
                .cartao(cartao)
                .build();

        when(buscaCartaoPort.findByNumero("1234")).thenReturn(Optional.of(cartao));

        assertThatThrownBy(() -> service.registrarCompra(transacao))
                .isInstanceOf(CartaoCanceladoException.class)
                .hasMessageContaining("Cartão está cancelado");

        verify(registraCartaoPort, never()).save(any());
        verify(registrarTransacaoPort, never()).registrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoLimiteInsuficiente() {
        Cartao cartao = Cartao.builder()
                .numero("1234")
                .limite(BigDecimal.valueOf(300))
                .limiteUtilizado(BigDecimal.valueOf(300))
                .status(StatusCartao.ATIVO)
                .build();

        Transacao transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(100))
                .cartao(cartao)
                .build();

        when(buscaCartaoPort.findByNumero("1234")).thenReturn(Optional.of(cartao));

        assertThatThrownBy(() -> service.registrarCompra(transacao))
                .isInstanceOf(LimiteExcedidoException.class)
                .hasMessage("Limite insuficiente para realizar a compra.");

        verify(registraCartaoPort, never()).save(any());
        verify(registrarTransacaoPort, never()).registrar(any());
    }
}
