package com.bytecard.adapter.out.persistence.transacao;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.out.transacao.BuscaTransacaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransacaoPortImpl implements RegistrarTransacaoPort , BuscaTransacaoPort {

    private final TransacaoRepository transacaoRepository;
    private final CartaoRepository cartaoRepository;

    public TransacaoPortImpl(TransacaoRepository transacaoRepository, CartaoRepository cartaoRepository) {
        this.transacaoRepository = transacaoRepository;
        this.cartaoRepository = cartaoRepository;
    }

    @Override
    public Transacao registrar(Transacao novaTransacao) {

       CartaoEntity cartao = cartaoRepository.findByNumero(novaTransacao.getCartao().getNumero()).get();

        TransacaoEntity transacaoEntity = TransacaoEntity.builder()
                .cartao(cartao)
                .valor(novaTransacao.getValor())
                .categoria(novaTransacao.getCategoria())
                .estabelecimento(novaTransacao.getEstabelecimento())
                .build();

        TransacaoEntity transacaoSalva = transacaoRepository.save(transacaoEntity);


        CartaoEntity cartaoUtilizado = transacaoSalva.getCartao();

        return  Transacao.builder()
                .id(transacaoSalva.getId())
                .valor(transacaoSalva.getValor())
                .estabelecimento(transacaoSalva.getEstabelecimento())
                .categoria(transacaoSalva.getCategoria())
                .data(transacaoSalva.getDataHora())
                .cartao(Cartao.builder()
                        .numero(cartaoUtilizado.getNumero())
                        .limite(cartaoUtilizado.getLimite())
                        .status(cartaoUtilizado.getStatus())
                        .validade(cartaoUtilizado.getValidade())
                        .build())
                .build();
    }

    @Override
    public List<Transacao> findByCartaoNumeroAndMes(String numeroCartao, Integer year, Integer monthValue) {

       var transacoes = transacaoRepository.findByCartaoNumeroAndMes(numeroCartao,year,monthValue);
       return transacoes.stream().map(trasacaoEntity ->
               Transacao.builder()
                       .id(trasacaoEntity.getId())
                       .valor(trasacaoEntity.getValor())
                       .estabelecimento(trasacaoEntity.getEstabelecimento())
                       .categoria(trasacaoEntity.getCategoria())
                       .data(trasacaoEntity.getDataHora())
                       .build())
               .toList();
    }
}