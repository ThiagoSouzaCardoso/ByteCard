package com.bytecard.adapter.out.persistence.cartao;

import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.model.StatusCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(CartaoPortImpl.class)
class CartaoPortImplIntegrationTest {

    @Autowired
    private CartaoPortImpl cartaoPort;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private ClienteRespository clienteRespository;

    private ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        cliente = ClienteEntity.builder()
                .nome("Maria Teste")
                .email("maria@bytecard.com")
                .cpf("12345678900")
                .senha("123456")
                .role("CLIENTE")
                .build();

        clienteRespository.save(cliente);
    }

    @Test
    void deveSalvarCartaoComSucesso() {
        Cartao novoCartao = Cartao.builder()
                .numero("9999888877776666")
                .cvv("321")
                .validade(YearMonth.of(2029, 6))
                .status(StatusCartao.ATIVO)
                .limite(BigDecimal.valueOf(2000))
                .limiteUtilizado(BigDecimal.ZERO)
                .cliente(Cliente.builder().email(cliente.getEmail()).build())
                .build();

        Cartao salvo = cartaoPort.save(novoCartao);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNumero()).isEqualTo("9999888877776666");
        assertThat(salvo.getCliente().email()).isEqualTo(cliente.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        Cartao cartao = Cartao.builder()
                .numero("1111222233334444")
                .cvv("123")
                .validade(YearMonth.now())
                .limite(BigDecimal.valueOf(1000))
                .status(StatusCartao.ATIVO)
                .limiteUtilizado(BigDecimal.ZERO)
                .cliente(Cliente.builder().email("naoexiste@teste.com").build())
                .build();

        assertThatThrownBy(() -> cartaoPort.save(cartao))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveBuscarCartaoPorNumero() {
        Cartao cartao = Cartao.builder()
                .numero("4444333322221111")
                .cvv("999")
                .validade(YearMonth.of(2031, 1))
                .limite(BigDecimal.valueOf(3000))
                .limiteUtilizado(BigDecimal.valueOf(0))
                .status(StatusCartao.ATIVO)
                .cliente(Cliente.builder().email(cliente.getEmail()).build())
                .build();

        cartaoPort.save(cartao);

        var encontrado = cartaoPort.findByNumero("4444333322221111");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNumero()).isEqualTo("4444333322221111");
    }

    @Test
    void deveBuscarTodosComFiltro() {
        Cartao cartao = Cartao.builder()
                .numero("5555666677778888")
                .cvv("555")
                .validade(YearMonth.of(2027, 5))
                .limite(BigDecimal.valueOf(4000))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.ATIVO)
                .cliente(Cliente.builder().email(cliente.getEmail()).build())
                .build();

        cartaoPort.save(cartao);

        Page<Cartao> pagina = cartaoPort.findAllOrdenadosPaginados(0, 5, cliente.getCpf(), null);

        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).getCliente().email()).isEqualTo(cliente.getEmail());
    }
}

