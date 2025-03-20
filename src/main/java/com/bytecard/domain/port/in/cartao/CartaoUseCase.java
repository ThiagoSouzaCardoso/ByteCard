package com.bytecard.domain.port.in.cartao;

import com.bytecard.domain.model.Cartao;

import java.math.BigDecimal;
import java.util.List;

public interface CartaoUseCase {

    Cartao register(Cartao cartao);

    List<Cartao> getAllCartoes();

    Cartao alterarLimit(BigDecimal limite, Long id);

    Cartao alterarStatusCartao(Long id, String status);


    }