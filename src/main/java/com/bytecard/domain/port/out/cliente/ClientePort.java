package com.bytecard.domain.port.out.cliente;

import com.bytecard.domain.model.Cliente;

public interface ClientePort {
    Cliente register(Cliente cliente);
}
