package com.bytecard.domain.port.in.cartao;

import com.bytecard.domain.model.Cartao;

import java.util.List;

public interface CartaoUseCase {

    Cartao register(Cartao cartao);

    List<Cartao> getAllCartoes();

}