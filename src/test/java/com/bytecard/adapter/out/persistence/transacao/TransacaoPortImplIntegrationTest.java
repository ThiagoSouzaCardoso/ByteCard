package com.bytecard.adapter.out.persistence.transacao;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TransacaoPortImpl.class)
class TransacaoPortImplIntegrationTest {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private TransacaoPortImpl transacaoPort;

    @Autowired
    private ClienteRespository clienteRespository;


    private Cartao cartao;

    @BeforeEach
    void setUp() {

        var cliente = clienteRespository.save(
                ClienteEntity.builder()
                        .nome("Maria Silva")
                        .cpf("12345678901")
                        .email("maria@bytecard.com")
                        .senha("senha123")
                        .role("CLIENTE")
                        .build()
        );


        var cartaoEntity = cartaoRepository.save(
                CartaoEntity.builder()
                        .numero("1234567890123456")
                        .cvv("123")
                        .cliente(cliente)
                        .validade(YearMonth.of(2026, 12))
                        .limite(new BigDecimal("1000.00"))
                        .limiteUtilizado(BigDecimal.ZERO)
                        .status(StatusCartao.ATIVO)
                        .build()
        );

        cartao = Cartao.builder()
                .numero(cartaoEntity.getNumero())
                .limite(cartaoEntity.getLimite())
                .status(cartaoEntity.getStatus())
                .validade(cartaoEntity.getValidade())
                .id(cartaoEntity.getId())
                .build();
    }

    @Test
    void deveRegistrarTransacaoComSucesso() {
        var transacao = Transacao.builder()
                .cartao(cartao)
                .valor(new BigDecimal("150.00"))
                .categoria(CategoriaTransacao.ALIMENTACAO)
                .estabelecimento("Lanchonete Teste")
                .build();

        Transacao resultado = transacaoPort.registrar(transacao);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualTo("150.00");
        assertThat(resultado.getEstabelecimento()).isEqualTo("Lanchonete Teste");
    }

    @Test
    void deveBuscarTransacoesPorCartaoEMes() {

        var transacao = Transacao.builder()
                .cartao(cartao)
                .valor(new BigDecimal("200.00"))
                .categoria(CategoriaTransacao.CASA)
                .estabelecimento("Mercado Central")
                .build();

        transacaoPort.registrar(transacao);

        int ano = OffsetDateTime.now().getYear();
        int mes = OffsetDateTime.now().getMonthValue();

        List<Transacao> resultado = transacaoPort.findByCartaoNumeroAndMes(cartao.getNumero(), ano, mes);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo(CategoriaTransacao.CASA);
    }

    @Test
    void deveLancarExcecaoAoRegistrarTransacaoComCartaoInexistente() {
        Transacao transacao = Transacao.builder()
                .cartao(Cartao.builder().numero("9999999999999999").build()) // número inexistente
                .valor(new BigDecimal("100.00"))
                .categoria(CategoriaTransacao.EDUCACAO)
                .estabelecimento("Escola Virtual")
                .build();

        assertThatThrownBy(() -> transacaoPort.registrar(transacao))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No value present");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverTransacoesNoPeriodo() {
        int ano = 2099;
        int mes = 1;

        List<Transacao> resultado = transacaoPort.findByCartaoNumeroAndMes(cartao.getNumero(), ano, mes);

        assertThat(resultado).isEmpty();
    }




}
