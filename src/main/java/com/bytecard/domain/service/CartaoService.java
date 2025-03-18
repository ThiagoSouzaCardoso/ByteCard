package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.springframework.stereotype.Service;

@Service
public class CartaoService implements CartaoUseCase {

    private final CartaoRepository cartaoRepository;

    public CartaoService(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }


}