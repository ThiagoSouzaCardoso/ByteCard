package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService implements TransacaoUseCase {

    private final CartaoRepository cartaoRepository;

    public TransacaoService(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }


}