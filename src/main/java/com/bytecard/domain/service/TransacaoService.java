package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoBloqueadoException;
import com.bytecard.domain.exception.CartaoCanceladoException;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.LimiteExcedidoException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransacaoService implements TransacaoUseCase {

    private final BuscaCartaoPort buscaCartaoPort;
    private final RegistraCartaoPort registraCartaoPort;
    private final RegistrarTransacaoPort registrarTransacaoPort;

    public TransacaoService(BuscaCartaoPort buscaCartaoPort,
                            RegistraCartaoPort registraCartaoPort,
                            RegistrarTransacaoPort registrarTransacaoPort) {
        this.buscaCartaoPort = buscaCartaoPort;
        this.registraCartaoPort = registraCartaoPort;
        this.registrarTransacaoPort = registrarTransacaoPort;
    }

    @Override
    public Transacao registrarCompra(Transacao transacao) {
        Cartao cartao = buscaCartaoPort.findByNumero(transacao.getCartao().getNumero())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        cartao.registrarCompra(transacao.getValor());

        registraCartaoPort.save(cartao);


        var novaTransacao = Transacao.builder()
                .valor(transacao.getValor())
                .categoria(transacao.getCategoria())
                .estabelecimento(transacao.getEstabelecimento())
                .cartao(cartao)
                .build();
       return  registrarTransacaoPort.registrar(novaTransacao);
    }

}