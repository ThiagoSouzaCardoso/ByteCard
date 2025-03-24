package com.bytecard.adapter.out.persistence.cliente;

import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ClientePortImpl.class)
class ClientePortImplIntegrationTest {

    @Autowired
    private ClienteRespository clienteRespository;

    @Autowired
    private ClientePortImpl clientePort;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        cliente = Cliente.builder()
                .nome("João da Silva")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE")
                .build();
    }

    @Test
    void deveRegistrarClienteComSucesso() {
        Cliente salvo = clientePort.register(cliente);

        assertThat(salvo).isNotNull();
        assertThat(salvo.nome()).isEqualTo("João da Silva");

        var clienteBanco = clienteRespository.findByEmail("joao@email.com");
        assertThat(clienteBanco).isPresent();
    }

    @Test
    void deveBuscarClientePorEmail() {
        clientePort.register(cliente);

        Optional<Cliente> encontrado = clientePort.findClienteByEmail("joao@email.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().cpf()).isEqualTo("12345678900");
    }

    @Test
    void deveRetornarOptionalVazioQuandoEmailNaoExiste() {
        Optional<Cliente> encontrado = clientePort.findClienteByEmail("nao@existe.com");
        assertThat(encontrado).isEmpty();
    }

    @Test
    void deveVerificarExistenciaDeClientePorEmailOuCpf() {
        clientePort.register(cliente);

        boolean existe = clientePort.existeClienteCadastrado("joao@email.com", "12345678900");
        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalsoQuandoClienteNaoExiste() {
        boolean existe = clientePort.existeClienteCadastrado("nao@existe.com", "00000000000");
        assertThat(existe).isFalse();
    }
}
