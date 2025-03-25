package com.bytecard.adapter.out.persistence.transacao;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransacaoPortImplTest {

    private TransacaoRepository transacaoRepository;
    private CartaoRepository cartaoRepository;
    private TransacaoPortImpl transacaoPort;

    @BeforeEach
    void setUp() {
        transacaoRepository = mock(TransacaoRepository.class);
        cartaoRepository = mock(CartaoRepository.class);
        transacaoPort = new TransacaoPortImpl(transacaoRepository, cartaoRepository);
    }

    @Test
    void deveRegistrarTransacaoComSucesso() {
        var cartaoEntity = CartaoEntity.builder()
                .id(1L)
                .numero("1234567890123456")
                .limite(BigDecimal.valueOf(1000))
                .limiteUtilizado(BigDecimal.valueOf(0))
                .status(StatusCartao.ATIVO)
                .validade(YearMonth.of(2026, 12))
                .build();

        var transacao = Transacao.builder()
                .valor(BigDecimal.valueOf(200))
                .categoria(CategoriaTransacao.ALIMENTACAO)
                .estabelecimento("Restaurante Central")
                .cartao(com.bytecard.domain.model.Cartao.builder()
                        .numero("1234567890123456")
                        .build())
                .build();

        var transacaoEntity = TransacaoEntity.builder()
                .id(1L)
                .cartao(cartaoEntity)
                .valor(BigDecimal.valueOf(200))
                .categoria(CategoriaTransacao.ALIMENTACAO)
                .estabelecimento("Restaurante Central")
                .dataHora(OffsetDateTime.now())
                .build();

        when(cartaoRepository.findByNumero("1234567890123456"))
                .thenReturn(Optional.of(cartaoEntity));
        when(transacaoRepository.save(any(TransacaoEntity.class)))
                .thenReturn(transacaoEntity);

        Transacao resultado = transacaoPort.registrar(transacao);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualByComparingTo("200");
        assertThat(resultado.getEstabelecimento()).isEqualTo("Restaurante Central");
        assertThat(resultado.getCategoria()).isEqualTo(CategoriaTransacao.ALIMENTACAO);
        assertThat(resultado.getCartao().getNumero()).isEqualTo("1234567890123456");

        verify(cartaoRepository).findByNumero("1234567890123456");
        verify(transacaoRepository).save(any(TransacaoEntity.class));
    }

    @Test
    void deveRetornarTransacoesPorCartaoEMes() {
        var transacaoEntity = TransacaoEntity.builder()
                .id(1L)
                .valor(BigDecimal.valueOf(150))
                .estabelecimento("Mercado XPTO")
                .categoria(CategoriaTransacao.CASA)
                .dataHora(OffsetDateTime.now())
                .build();

        when(transacaoRepository.findByCartaoNumeroAndMes("1234567890123456", 2024, 3))
                .thenReturn(List.of(transacaoEntity));

        List<Transacao> resultado = transacaoPort.findByCartaoNumeroAndMes("1234567890123456", 2024, 3);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo(CategoriaTransacao.CASA);
        assertThat(resultado.get(0).getEstabelecimento()).isEqualTo("Mercado XPTO");

        verify(transacaoRepository).findByCartaoNumeroAndMes("1234567890123456", 2024, 3);
    }
}

