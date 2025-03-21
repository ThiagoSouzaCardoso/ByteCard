package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService implements TransacaoUseCase {

    private final CartaoRepository cartaoRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(CartaoRepository cartaoRepository, TransacaoRepository transacaoRepository) {
        this.cartaoRepository = cartaoRepository;
        this.transacaoRepository = transacaoRepository;
    }


    @Override
    public Transacao registrarCompra(Transacao transacao) {

        CartaoEntity cartao = cartaoRepository.findByNumero(transacao.getCartao().getNumero())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        TransacaoEntity transacaoEntity = TransacaoEntity.builder()
                .cartao(cartao)
                .valor(transacao.getValor())
                .categoria(transacao.getCategoria())
                .estabelecimento(transacao.getEstabelecimento())
                .build();

         TransacaoEntity transacaoSalva = transacaoRepository.save(transacaoEntity);

        return  Transacao.builder()
                 .id(transacaoSalva.getId())
                 .valor(transacaoSalva.getValor())
                 .estabelecimento(transacaoSalva.getEstabelecimento())
                 .categoria(transacaoSalva.getCategoria())
                 .data(transacaoSalva.getDataHora())
                 .cartao(transacao.getCartao())
                 .build();
    }
}