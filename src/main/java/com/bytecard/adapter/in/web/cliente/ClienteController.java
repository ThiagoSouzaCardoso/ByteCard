package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController implements ClienteControllerSwagger{

    private final ClienteUseCase clienteUseCase;

    public ClienteController(ClienteUseCase clienteUseCase) {
        this.clienteUseCase = clienteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@Valid @RequestBody NovoClienteRequest novoClienteRequest) {
        clienteUseCase.register(novoClienteRequest.toModel());
    }

}
