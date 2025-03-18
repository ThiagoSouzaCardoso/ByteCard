package com.bytecard.adapter.in.web.cartao;

import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    private final CartaoUseCase cartaoUseCase;

    public CartaoController(CartaoUseCase cartaoUseCase) {
        this.cartaoUseCase = cartaoUseCase;
    }




}