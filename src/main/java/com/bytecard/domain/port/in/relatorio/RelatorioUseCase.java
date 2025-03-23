package com.bytecard.domain.port.in.relatorio;

import com.bytecard.domain.model.CriarRelatorio;
import com.bytecard.domain.model.RelatorioGastos;

public interface RelatorioUseCase {

    RelatorioGastos gerarRelatorioPorCategoria(CriarRelatorio request);

}
