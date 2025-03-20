package com.bytecard.domain.port.in.cartao;

import com.bytecard.domain.model.Cartao;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface CartaoUseCase {

    Cartao register(Cartao cartao);

    Page<Cartao> getAllCartoes(Integer pageNo, Integer pageSize);

    Cartao alterarLimit(BigDecimal limite, Long id);

    Cartao alterarStatusCartao(Long id, String status);


    }