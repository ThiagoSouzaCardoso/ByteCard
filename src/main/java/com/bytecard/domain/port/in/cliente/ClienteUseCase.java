package com.bytecard.domain.port.in.cliente;

import com.bytecard.domain.model.Cliente;

public interface ClienteUseCase {
    Cliente register(Cliente cliente);

}
