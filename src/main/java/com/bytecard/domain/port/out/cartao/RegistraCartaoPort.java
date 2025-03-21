package com.bytecard.domain.port.out.cartao;

import com.bytecard.domain.model.Cartao;

public interface RegistraCartaoPort {

    Cartao save(Cartao cartao);
}
