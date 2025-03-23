package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.CriarRelatorio;
import com.bytecard.domain.model.GastoCategoria;
import com.bytecard.domain.model.RelatorioGastos;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import com.bytecard.domain.port.out.transacao.RelatorioTransacaoPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService implements TransacaoUseCase {

    private final BuscaCartaoPort buscaCartaoPort;
    private final RegistrarTransacaoPort registrarTransacaoPort;
    private final RelatorioTransacaoPort relatorioTransacaoPort;

    public TransacaoService(BuscaCartaoPort buscaCartaoPort, RegistrarTransacaoPort registrarTransacaoPort, TransacaoRepository transacaoRepository, RelatorioTransacaoPort relatorioTransacaoPort) {
        this.buscaCartaoPort = buscaCartaoPort;
        this.registrarTransacaoPort = registrarTransacaoPort;
        this.relatorioTransacaoPort = relatorioTransacaoPort;
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

    public RelatorioGastos gerarRelatorioPorCategoria(CriarRelatorio request) {
        Integer ano = request.mesAno().getYear();
        Integer mes = request.mesAno().getMonthValue();

        var cartao = buscaCartaoPort.findByNumero(request.numeroCartao())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        List<GastoCategoria>  gastos = relatorioTransacaoPort.getSomatorioGastosPorCategoriaNoMes(cartao.getId(), ano, mes);

        if (gastos.isEmpty()) {
            throw new RelatorioEmptyException("Nenhuma compra realizada no período.");
        }

        BigDecimal total = gastos.stream()
                .map(GastoCategoria::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioGastos(
                cartao.getNumero(),
                String.format("%02d/%04d", mes, ano),
                gastos,
                total
        );
    }
}