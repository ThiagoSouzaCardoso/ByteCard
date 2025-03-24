package com.bytecard.adapter.out.persistence;

import com.bytecard.adapter.out.persistence.cartao.CartaoPortImpl;
import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.model.StatusCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartaoPortImplTest {

    private CartaoRepository cartaoRepository;
    private ClienteRespository clienteRespository;
    private CartaoPortImpl cartaoPort;

    @BeforeEach
    void setUp() {
        cartaoRepository = mock(CartaoRepository.class);
        clienteRespository = mock(ClienteRespository.class);
        cartaoPort = new CartaoPortImpl(cartaoRepository, clienteRespository);
    }

    @Test
    void deveSalvarCartaoComSucesso() {
        ClienteEntity clienteEntity = ClienteEntity.builder()
                .id(1L)
                .nome("João")
                .email("joao@email.com")
                .build();

        Cartao cartao = Cartao.builder()
                .numero("1234567890123456")
                .cvv("123")
                .validade(YearMonth.of(2030, 6))
                .status(StatusCartao.ATIVO)
                .limite(BigDecimal.valueOf(1000))
                .limiteUtilizado(BigDecimal.ZERO)
                .cliente(Cliente.builder().email("joao@email.com").build())
                .build();

        CartaoEntity cartaoSalvo = CartaoEntity.builder()
                .id(10L)
                .numero(cartao.getNumero())
                .cvv(cartao.getCvv())
                .validade(cartao.getValidade())
                .limite(cartao.getLimite())
                .limiteUtilizado(cartao.getLimiteUtilizado())
                .status(cartao.getStatus())
                .cliente(clienteEntity)
                .build();

        when(clienteRespository.findByEmail("joao@email.com")).thenReturn(Optional.of(clienteEntity));
        when(cartaoRepository.save(any())).thenReturn(cartaoSalvo);

        Cartao resultado = cartaoPort.save(cartao);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNumero()).isEqualTo(cartao.getNumero());
        verify(cartaoRepository).save(any(CartaoEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        Cartao cartao = Cartao.builder()
                .cliente(Cliente.builder().email("inexistente@email.com").build())
                .build();

        when(clienteRespository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartaoPort.save(cartao))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveBuscarCartaoPorNumero() {
        CartaoEntity entity = CartaoEntity.builder()
                .id(1L)
                .numero("1234567890123456")
                .cvv("321")
                .validade(YearMonth.of(2025, 12))
                .limite(BigDecimal.valueOf(5000))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.ATIVO)
                .cliente(ClienteEntity.builder().email("email@email.com").nome("Carlos").build())
                .build();

        when(cartaoRepository.findByNumero("1234567890123456")).thenReturn(Optional.of(entity));

        Optional<Cartao> cartao = cartaoPort.findByNumero("1234567890123456");

        assertThat(cartao).isPresent();
        assertThat(cartao.get().getNumero()).isEqualTo("1234567890123456");
    }

    @Test
    void deveRetornarVazioQuandoBuscarCartaoPorNumeroInexistente() {
        when(cartaoRepository.findByNumero("0000000000000000")).thenReturn(Optional.empty());

        Optional<Cartao> cartao = cartaoPort.findByNumero("0000000000000000");

        assertThat(cartao).isEmpty();
    }

    @Test
    void deveBuscarTodosCartoesPaginadosOrdenados() {
        Pageable pageable = PageRequest.of(0, 2);
        CartaoEntity entity = CartaoEntity.builder()
                .id(1L)
                .numero("1234567890123456")
                .cvv("123")
                .validade(YearMonth.now())
                .limite(BigDecimal.valueOf(1000))
                .limiteUtilizado(BigDecimal.ZERO)
                .status(StatusCartao.ATIVO)
                .cliente(ClienteEntity.builder().nome("Cliente Teste").email("teste@bytecard.com").build())
                .build();

        Page<CartaoEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(cartaoRepository.findAllOrdered(null, null, pageable)).thenReturn(page);

        Page<Cartao> resultado = cartaoPort.findAllOrdenadosPaginados(0, 2, null, null);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getNumero()).isEqualTo("1234567890123456");
    }

    @Test
    void deveBuscarCartaoPorId() {
        CartaoEntity entity = CartaoEntity.builder()
                .id(7L)
                .numero("1234567890123456")
                .cvv("123")
                .validade(YearMonth.of(2025, 6))
                .limite(BigDecimal.valueOf(2000))
                .limiteUtilizado(BigDecimal.valueOf(0))
                .status(StatusCartao.ATIVO)
                .cliente(ClienteEntity.builder().nome("Joana").email("joana@email.com").build())
                .build();

        when(cartaoRepository.findById(7L)).thenReturn(Optional.of(entity));

        Optional<Cartao> resultado = cartaoPort.findById(7L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(7L);
        assertThat(resultado.get().getCliente().nome()).isEqualTo("Joana");
    }
}

