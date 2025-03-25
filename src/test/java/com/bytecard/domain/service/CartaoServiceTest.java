package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.model.*;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import com.bytecard.domain.port.out.transacao.BuscaTransacaoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static com.bytecard.domain.model.CategoriaTransacao.ALIMENTACAO;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartaoServiceTest {

    @Mock private BuscaClientePort buscaClientePort;
    @Mock private BuscaCartaoPort buscaCartaoPort;
    @Mock private RegistraCartaoPort registraCartaoPort;
    @Mock private BuscaTransacaoPort buscaTransacaoPort;

    @InjectMocks
    private CartaoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new CartaoService(buscaClientePort, buscaCartaoPort, registraCartaoPort, buscaTransacaoPort);
    }

    @Test
    void deveRegistrarCartaoComSucesso() {
        Cliente cliente = Cliente.builder().email("cliente@bytecard.com").nome("Cliente Teste").build();
        Cartao cartao = Cartao.builder().limite(BigDecimal.valueOf(1000)).cliente(cliente).build();

        when(buscaClientePort.findClienteByEmail("cliente@bytecard.com"))
                .thenReturn(Optional.of(cliente));

        when(registraCartaoPort.save(any(Cartao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cartao registrado = service.register(cartao);

        assertThat(registrado).isNotNull();
        assertThat(registrado.getNumero()).hasSize(16);
        assertThat(registrado.getCvv()).hasSize(3);
        assertThat(registrado.getStatus()).isEqualTo(StatusCartao.ATIVO);
        assertThat(registrado.getCliente()).isEqualTo(cliente);
    }

    @Test
    void deveLancarExcecaoSeClienteNaoExiste() {
        Cartao cartao = Cartao.builder()
                .cliente(Cliente.builder().email("naoexiste@bytecard.com").build())
                .limite(BigDecimal.valueOf(1000)).build();

        when(buscaClientePort.findClienteByEmail("naoexiste@bytecard.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(cartao))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveAlterarLimiteDoCartao() {
        Cartao cartao = Cartao.builder().numero("1234567890123456").limite(BigDecimal.valueOf(500)).build();

        when(buscaCartaoPort.findByNumero("1234567890123456")).thenReturn(Optional.of(cartao));
        when(registraCartaoPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Cartao atualizado = service.alterarLimit(BigDecimal.valueOf(800), "1234567890123456");

        assertThat(atualizado.getLimite()).isEqualByComparingTo("800");
    }

    @Test
    void deveLancarExcecaoSeCartaoNaoEncontradoAoAlterarLimite() {
        when(buscaCartaoPort.findByNumero("0000000000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.alterarLimit(BigDecimal.valueOf(1000), "0000000000000000"))
                .isInstanceOf(CartaoNotFoundException.class)
                .hasMessage("Cartão não Encontrado");
    }

    @Test
    void deveGerarFaturaComTransacoes() {
        String numero = "1234567890123456";
        YearMonth mes = YearMonth.of(2024, 12);

        Cartao cartao = Cartao.builder().numero(numero).cliente(Cliente.builder().nome("João").build()).build();

        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .valor(BigDecimal.valueOf(100))
                        .categoria(ALIMENTACAO)
                        .estabelecimento("Mercado")
                        .data(OffsetDateTime.now())
                        .build()
        );

        when(buscaCartaoPort.findByNumero(numero)).thenReturn(Optional.of(cartao));
        when(buscaTransacaoPort.findByCartaoNumeroAndMes(eq(numero), eq(2024), eq(12)))
                .thenReturn(transacoes);

        Fatura fatura = service.gerarFaturaPorNumero(numero, mes);

        assertThat(fatura).isNotNull();
        assertThat(fatura.valorTotal()).isEqualByComparingTo("100");
        assertThat(fatura.compras()).hasSize(1);
    }

    @Test
    void deveLancarExcecaoSeCartaoNaoExisteNaFatura() {
        when(buscaCartaoPort.findByNumero("0000000000000000"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.gerarFaturaPorNumero("0000000000000000", YearMonth.now()))
                .isInstanceOf(CartaoNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoSeNaoHouverTransacoesNaFatura() {
        String numero = "1234567890123456";
        YearMonth mes = YearMonth.of(2024, 12);

        Cartao cartao = Cartao.builder().numero(numero).cliente(Cliente.builder().nome("João").build()).build();

        when(buscaCartaoPort.findByNumero(numero)).thenReturn(Optional.of(cartao));
        when(buscaTransacaoPort.findByCartaoNumeroAndMes(numero, 2024, 12))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.gerarFaturaPorNumero(numero, mes))
                .isInstanceOf(RelatorioEmptyException.class)
                .hasMessage("Nenhuma compra realizada no período.");
    }

    @Test
    void deveAlterarStatusCartaoComSucesso() {
        Cartao cartao = Cartao.builder()
                .numero("1234567890123456")
                .status(StatusCartao.ATIVO)
                .build();

        when(buscaCartaoPort.findByNumero("1234567890123456"))
                .thenReturn(Optional.of(cartao));
        when(registraCartaoPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Cartao atualizado = service.alterarStatusCartao("1234567890123456", StatusCartao.BLOQUEADO);

        assertThat(atualizado.getStatus()).isEqualTo(StatusCartao.BLOQUEADO);
    }

    @Test
    void deveRetornarCartoesPaginados() {
        Cartao cartao1 = Cartao.builder().numero("1234567890123456").build();
        Cartao cartao2 = Cartao.builder().numero("6543210987654321").build();

        Page<Cartao> page = new PageImpl<>(List.of(cartao1, cartao2));

        when(buscaCartaoPort.findAllOrdenadosPaginados(0, 5, null, null)).thenReturn(page);

        Page<Cartao> resultado = service.getAllCartoes(0, 5, null, null);

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).contains(cartao1, cartao2);
    }

    @Test
    void deveGerarNumeroCartaoDe16DigitosUnico() {
        String numero1 = invokePrivateCartaoNumber();
        String numero2 = invokePrivateCartaoNumber();

        assertThat(numero1).hasSize(16).containsOnlyDigits();
        assertThat(numero2).hasSize(16).isNotEqualTo(numero1);
    }

    @Test
    void deveGerarCVVCom3Digitos() {
        String cvv = invokePrivateCvv();
        assertThat(cvv).hasSize(3).containsOnlyDigits();
    }

    private String invokePrivateCartaoNumber() {
        try {
            var method = CartaoService.class.getDeclaredMethod("gerarNumeroCartao");
            method.setAccessible(true);
            return (String) method.invoke(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokePrivateCvv() {
        try {
            var method = CartaoService.class.getDeclaredMethod("gerarCVV");
            method.setAccessible(true);
            return (String) method.invoke(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}