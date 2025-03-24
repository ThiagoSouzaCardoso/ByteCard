package com.bytecard.adapter.out.persistence.cliente;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientePortImplTest {

    private ClienteRespository clienteRespository;
    private ClientePortImpl clientePort;

    @BeforeEach
    void setUp() {
        clienteRespository = mock(ClienteRespository.class);
        clientePort = new ClientePortImpl(clienteRespository);
    }

    @Test
    void deveRegistrarClienteComSucesso() {
        Cliente cliente = Cliente.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE")
                .build();

        ClienteEntity clienteEntitySalvo = ClienteEntity.builder()
                .id(1L)
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .role("CLIENTE")
                .build();

        when(clienteRespository.save(any(ClienteEntity.class))).thenReturn(clienteEntitySalvo);

        Cliente salvo = clientePort.register(cliente);

        assertThat(salvo.nome()).isEqualTo("João");
        assertThat(salvo.email()).isEqualTo("joao@email.com");

        verify(clienteRespository).save(any(ClienteEntity.class));
    }

    @Test
    void deveRetornarClienteAoBuscarPorEmail() {
        ClienteEntity entity = ClienteEntity.builder()
                .nome("Maria")
                .cpf("98765432100")
                .email("maria@email.com")
                .senha("senha321")
                .build();

        when(clienteRespository.findByEmail("maria@email.com")).thenReturn(Optional.of(entity));

        Optional<Cliente> resultado = clientePort.findClienteByEmail("maria@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("Maria");
        assertThat(resultado.get().cpf()).isEqualTo("98765432100");
    }

    @Test
    void deveRetornarOptionalVazioQuandoEmailNaoExiste() {
        when(clienteRespository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clientePort.findClienteByEmail("nao@existe.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveVerificarExistenciaDeClientePorEmailOuCpf() {
        when(clienteRespository.existsByEmailOrCpf("teste@bytecard.com", "12345678900"))
                .thenReturn(true);

        boolean existe = clientePort.existeClienteCadastrado("teste@bytecard.com", "12345678900");

        assertThat(existe).isTrue();
        verify(clienteRespository).existsByEmailOrCpf("teste@bytecard.com", "12345678900");
    }
}

