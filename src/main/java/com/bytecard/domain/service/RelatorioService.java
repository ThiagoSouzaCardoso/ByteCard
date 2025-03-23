package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.model.CriarRelatorio;
import com.bytecard.domain.model.GastoCategoria;
import com.bytecard.domain.model.RelatorioGastos;
import com.bytecard.domain.port.in.relatorio.RelatorioUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.transacao.RelatorioTransacaoPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RelatorioService implements RelatorioUseCase {

    private final BuscaCartaoPort buscaCartaoPort;
    private final RelatorioTransacaoPort relatorioTransacaoPort;

    public RelatorioService(BuscaCartaoPort buscaCartaoPort, RelatorioTransacaoPort relatorioTransacaoPort) {
        this.buscaCartaoPort = buscaCartaoPort;
        this.relatorioTransacaoPort = relatorioTransacaoPort;
    }


    @Override
    public RelatorioGastos gerarRelatorioPorCategoria(CriarRelatorio request) {
        Integer ano = request.mesAno().getYear();
        Integer mes = request.mesAno().getMonthValue();

        var cartao = buscaCartaoPort.findByNumero(request.numeroCartao())
                .orElseThrow(() -> new CartaoNotFoundException("Cartão não encontrado"));

        List<GastoCategoria> gastos = relatorioTransacaoPort.getSomatorioGastosPorCategoriaNoMes(cartao.getId(), ano, mes);

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
