package com.bytecard.domain.service;

import com.bytecard.adapter.in.web.transacao.inputs.RelatorioGastosRequest;
import com.bytecard.adapter.in.web.transacao.outputs.GastoPorCategoriaResponse;
import com.bytecard.adapter.in.web.transacao.outputs.RelatorioGastosResponse;
import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public RelatorioGastosResponse gerarRelatorioPorCategoria(RelatorioGastosRequest request) {
        int ano = request.mesAno().getYear();
        int mes = request.mesAno().getMonthValue();

        var cartao = cartaoRepository.findByNumero(request.numeroCartao())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        var gastos = transacaoRepository.somarGastosPorCategoriaNoMes(cartao.getId(), ano, mes);

        if (gastos.isEmpty()) {
            throw new RuntimeException("Nenhuma compra realizada no período.");
        }

        BigDecimal total = gastos.stream()
                .map(GastoPorCategoriaResponse::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioGastosResponse(
                cartao.getNumero(),
                String.format("%02d/%04d", mes, ano),
                gastos,
                total
        );
    }
}