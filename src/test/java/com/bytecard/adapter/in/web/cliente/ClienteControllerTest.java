
package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void deveCadastrarNovoClienteComSucesso() {
        // given
        NovoClienteRequest request = new NovoClienteRequest(
                "Maria",
                "12345678900",
                "maria@email.com",
                "senha123",
                "CLIENTE"
        );

        clienteController.registerUser(request);

        verify(clienteUseCase).register(any());
    }
}
