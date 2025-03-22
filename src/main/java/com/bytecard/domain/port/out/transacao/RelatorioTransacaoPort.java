package com.bytecard.domain.port.out.transacao;

import com.bytecard.domain.model.GastoCategoria;

import java.util.List;

public interface RelatorioTransacaoPort {

    List<GastoCategoria> getSomatorioGastosPorCategoriaNoMes(Long id, Integer ano, Integer mes);

}
