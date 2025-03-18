package com.bytecard.adapter.out.persistence.cartao;

import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.domain.port.out.cartao.CartaoPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CartaoPortImpl implements CartaoPort {

    private CartaoRepository cartaoRepository;


}