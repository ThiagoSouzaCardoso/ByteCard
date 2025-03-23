package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService implements TransacaoUseCase {

    private final BuscaCartaoPort buscaCartaoPort;
    private final RegistrarTransacaoPort registrarTransacaoPort;

    public TransacaoService(BuscaCartaoPort buscaCartaoPort, RegistrarTransacaoPort registrarTransacaoPort) {
        this.buscaCartaoPort = buscaCartaoPort;
        this.registrarTransacaoPort = registrarTransacaoPort;
    }

//TODO ADICIONAR LIMITE UTILIZADO
    @Override
    public Transacao registrarCompra(Transacao transacao) {
        Cartao cartao = buscaCartaoPort.findByNumero(transacao.getCartao().getNumero())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        var novaaTransacao = Transacao.builder()
                .valor(transacao.getValor())
                .categoria(transacao.getCategoria())
                .estabelecimento(transacao.getEstabelecimento())
                .cartao(cartao)
                .build();
       return  registrarTransacaoPort.registrar(novaaTransacao);
    }


}