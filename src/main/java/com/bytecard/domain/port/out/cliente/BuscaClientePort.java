package com.bytecard.domain.port.out.cliente;

import com.bytecard.domain.model.Cliente;

import java.util.Optional;

public interface BuscaClientePort {

    Optional<Cliente> findClienteByEmail(String email);

}
