package com.bytecard.domain.port.in.cartao;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface CartaoUseCase {

    Cartao register(Cartao cartao);

    Page<Cartao> getAllCartoes(Integer pageNo, Integer pageSize,String cpf, String numeroCartao);

    Cartao alterarLimit(BigDecimal limite, Long id);

    Cartao alterarStatusCartao(Long id, StatusCartao status);


    }