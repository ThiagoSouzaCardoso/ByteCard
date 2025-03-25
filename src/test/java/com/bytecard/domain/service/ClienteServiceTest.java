package com.bytecard.domain.service;

import com.bytecard.domain.exception.UserAlreadyExistException;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import com.bytecard.domain.port.out.cliente.RegistraClientePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClienteServiceTest {

    private RegistraClientePort registraClientePort;
    private BuscaClientePort buscaClientePort;
    private PasswordEncoder passwordEncoder;
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        registraClientePort = mock(RegistraClientePort.class);
        buscaClientePort = mock(BuscaClientePort.class);
        passwordEncoder = mock(PasswordEncoder.class);
        clienteService = new ClienteService(registraClientePort, buscaClientePort, passwordEncoder);
    }

    @Test
    void deveRegistrarClienteComSenhaCriptografadaQuandoNaoExistente() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Maria")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("senha123")
                .papel("ROLE_USER")
                .build();

        when(buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha_criptografada");

        Cliente clienteComSenhaCriptografada = Cliente.builder()
                .nome("Maria")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("senha_criptografada")
                .papel("ROLE_USER")
                .build();

        when(registraClientePort.register(any(Cliente.class))).thenReturn(clienteComSenhaCriptografada);

        // Act
        Cliente resultado = clienteService.register(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals("Maria", resultado.nome());
        assertEquals("12345678900", resultado.cpf());
        assertEquals("maria@email.com", resultado.email());
        assertEquals("senha_criptografada", resultado.senha());
        assertEquals("ROLE_USER", resultado.papel());

        verify(buscaClientePort).existeClienteCadastrado("maria@email.com", "12345678900");
        verify(passwordEncoder).encode("senha123");
        verify(registraClientePort).register(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteJaExistir() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("João")
                .cpf("11122233344")
                .email("joao@email.com")
                .senha("123456")
                .papel("ROLE_USER")
                .build();

        when(buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf())).thenReturn(true);

        // Act + Assert
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {
            clienteService.register(cliente);
        });

        assertEquals("Usuário já Cadastrado!", exception.getMessage());
        verify(buscaClientePort).existeClienteCadastrado("joao@email.com", "11122233344");
        verify(passwordEncoder, never()).encode(anyString());
        verify(registraClientePort, never()).register(any());
    }

    @Test
    void deveLancarNullPointerExceptionSeClienteForNulo() {
        // Arrange, Act + Assert
        assertThrows(NullPointerException.class, () -> {
            clienteService.register(null);
        });

        verify(buscaClientePort, never()).existeClienteCadastrado(any(), any());
        verify(passwordEncoder, never()).encode(any());
        verify(registraClientePort, never()).register(any());
    }

    @Test
    void deveLancarExceptionSeSenhaForNula() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Ana")
                .cpf("00011122233")
                .email("ana@email.com")
                .senha(null)
                .papel("ROLE_USER")
                .build();

        when(buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf())).thenReturn(false);
        when(passwordEncoder.encode(null)).thenThrow(new IllegalArgumentException("Senha inválida"));

        // Act + Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.register(cliente);
        });

        assertEquals("Senha inválida", exception.getMessage());
        verify(passwordEncoder).encode(null);
        verify(registraClientePort, never()).register(any());
    }

    @Test
    void devePropagarExcecaoDoPortDePersistencia() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Carlos")
                .cpf("99988877766")
                .email("carlos@email.com")
                .senha("abc123")
                .papel("ROLE_ADMIN")
                .build();

        when(buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf())).thenReturn(false);
        when(passwordEncoder.encode("abc123")).thenReturn("senhaCriptografada");
        when(registraClientePort.register(any())).thenThrow(new RuntimeException("Erro ao persistir cliente"));

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.register(cliente);
        });

        assertEquals("Erro ao persistir cliente", exception.getMessage());
        verify(registraClientePort).register(any());
    }

    @Test
    void devePreservarTodosOsCamposDoClienteAoRegistrar() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Lucas")
                .cpf("44455566677")
                .email("lucas@email.com")
                .senha("minhaSenha")
                .papel("ROLE_USER")
                .build();

        when(buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf())).thenReturn(false);
        when(passwordEncoder.encode("minhaSenha")).thenReturn("senhaSegura");

        Cliente clienteRegistrado = Cliente.builder()
                .nome("Lucas")
                .cpf("44455566677")
                .email("lucas@email.com")
                .senha("senhaSegura")
                .papel("ROLE_USER")
                .build();

        when(registraClientePort.register(any())).thenReturn(clienteRegistrado);

        // Act
        Cliente resultado = clienteService.register(cliente);

        // Assert
        assertEquals("Lucas", resultado.nome());
        assertEquals("44455566677", resultado.cpf());
        assertEquals("lucas@email.com", resultado.email());
        assertEquals("senhaSegura", resultado.senha());
        assertEquals("ROLE_USER", resultado.papel());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(registraClientePort).register(captor.capture());

        Cliente capturado = captor.getValue();
        assertEquals("Lucas", capturado.nome());
        assertEquals("44455566677", capturado.cpf());
        assertEquals("lucas@email.com", capturado.email());
        assertEquals("senhaSegura", capturado.senha());
        assertEquals("ROLE_USER", capturado.papel());
    }
}

