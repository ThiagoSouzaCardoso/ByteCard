package com.bytecard.adapter.out.persistence.cliente;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.exception.UserAlreadyExistException;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import com.bytecard.domain.port.out.cliente.RegistraClientePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientePortImpl implements RegistraClientePort, BuscaClientePort {

    private final ClienteRespository clienteRespository;

    public ClientePortImpl(ClienteRespository clienteRespository) {
        this.clienteRespository = clienteRespository;
    }

    @Override
    public Cliente register(Cliente cliente) {

       if(clienteRespository.findByEmail(cliente.email()).isPresent()){
           throw new UserAlreadyExistException("Usuário já Cadastrado!");
       }

        var clienteEntity = new ClienteEntity();
       clienteEntity.setCpf(cliente.cpf());
       clienteEntity.setNome(cliente.nome());
       clienteEntity.setEmail(cliente.email());
       clienteEntity.setSenha(cliente.senha());
       clienteEntity.setRole(cliente.papel().toUpperCase());

        ClienteEntity clienteSaved = clienteRespository.save(clienteEntity);

        return Cliente.builder()
                .cpf(clienteSaved.getCpf())
                .email(clienteSaved.getEmail())
                .nome(clienteSaved.getNome())
                .senha(clienteSaved.getSenha())
                .build();
    }


    @Override
    public Optional<Cliente> findClienteByEmail(String email) {
        return clienteRespository.findByEmail(email)
                .map(clienteEntity -> Cliente.builder()
                        .cpf(clienteEntity.getCpf())
                        .email(clienteEntity.getEmail())
                        .nome(clienteEntity.getNome())
                        .senha(clienteEntity.getSenha())
                        .build()
                );
    }
}
