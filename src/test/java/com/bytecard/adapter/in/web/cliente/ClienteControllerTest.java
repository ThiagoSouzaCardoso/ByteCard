package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import com.bytecard.domain.exception.UserAlreadyExistException;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteController - Unit")
class ClienteControllerTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @InjectMocks
    private ClienteController clienteController;

    @Nested
    @DisplayName("Cadastro de Cliente")
    class CadastroCliente {

        @Test
        @DisplayName("Deve chamar use case para cadastrar novo cliente com sucesso")
        void deveCadastrarNovoClienteComSucesso() {
            NovoClienteRequest request = new NovoClienteRequest(
                    "Maria",
                    "12345678900",
                    "maria@email.com",
                    "senha123",
                    "CLIENTE"
            );

            clienteController.registerUser(request);

            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(clienteUseCase).register(captor.capture());

            Cliente capturado = captor.getValue();
            assertThat(capturado.nome()).isEqualTo("Maria");
            assertThat(capturado.cpf()).isEqualTo("12345678900");
            assertThat(capturado.email()).isEqualTo("maria@email.com");
            assertThat(capturado.senha()).isEqualTo("senha123");
            assertThat(capturado.papel()).isEqualTo("CLIENTE");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar cadastrar cliente já existente")
        void deveLancarExcecaoQuandoClienteJaExiste() {
            NovoClienteRequest request = new NovoClienteRequest(
                    "Maria",
                    "12345678900",
                    "maria@email.com",
                    "senha123",
                    "CLIENTE"
            );

            doThrow(new UserAlreadyExistException("Usuário já Cadastrado!")).when(clienteUseCase).register(org.mockito.ArgumentMatchers.any());

            assertThatThrownBy(() -> clienteController.registerUser(request))
                    .isInstanceOf(UserAlreadyExistException.class)
                    .hasMessage("Usuário já Cadastrado!");

            verify(clienteUseCase).register(org.mockito.ArgumentMatchers.any());
        }
    }
}