package com.bytecard.domain.port.in.transacao;

import com.bytecard.domain.model.CriarRelatorio;
import com.bytecard.domain.model.RelatorioGastos;
import com.bytecard.domain.model.Transacao;

public interface TransacaoUseCase {
    Transacao registrarCompra(Transacao dto);

    RelatorioGastos gerarRelatorioPorCategoria(CriarRelatorio request);
}
