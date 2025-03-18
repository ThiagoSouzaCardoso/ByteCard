package com.bytecard.domain.service;

import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import com.bytecard.domain.port.out.cliente.ClientePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements ClienteUseCase {

    private final ClientePort clientePort;
   private final PasswordEncoder passwordEncoder;


    public ClienteService(ClientePort clientePort, PasswordEncoder passwordEncoder) {
        this.clientePort = clientePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cliente findClienteByEmail(String email) {
        return null;
    }

    @Override
    public Cliente register(Cliente cliente) {

        var clienteWithPassEncoder = Cliente.builder()
                .cpf(cliente.cpf())
                .email(cliente.email())
                .nome(cliente.nome())
                .senha(passwordEncoder.encode(cliente.senha()))
                .papel(cliente.papel())
                .build();
        System.out.println(clienteWithPassEncoder.senha());
       return clientePort.register(clienteWithPassEncoder);
    }
}
