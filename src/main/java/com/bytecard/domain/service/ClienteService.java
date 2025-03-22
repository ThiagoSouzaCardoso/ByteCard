package com.bytecard.domain.service;

import com.bytecard.domain.exception.UserAlreadyExistException;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import com.bytecard.domain.port.out.cliente.RegistraClientePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements ClienteUseCase {

    private final RegistraClientePort registraClientePort;
    private final BuscaClientePort buscaClientePort;
   private final PasswordEncoder passwordEncoder;


    public ClienteService(RegistraClientePort registraClientePort, BuscaClientePort buscaClientePort, PasswordEncoder passwordEncoder) {
        this.registraClientePort = registraClientePort;
        this.buscaClientePort = buscaClientePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cliente register(Cliente cliente) {

        boolean existeClienteCadastrado = buscaClientePort.existeClienteCadastrado(cliente.email(), cliente.cpf());

        if (existeClienteCadastrado) {
            throw new UserAlreadyExistException("Usuário já Cadastrado!");
        }

        var clienteWithPassEncoder = Cliente.builder()
                .cpf(cliente.cpf())
                .email(cliente.email())
                .nome(cliente.nome())
                .senha(passwordEncoder.encode(cliente.senha()))
                .papel(cliente.papel())
                .build();

       return registraClientePort.register(clienteWithPassEncoder);
    }
}
