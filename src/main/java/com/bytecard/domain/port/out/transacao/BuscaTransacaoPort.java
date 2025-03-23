package com.bytecard.domain.port.out.transacao;

import com.bytecard.domain.model.Transacao;

import java.util.List;

public interface BuscaTransacaoPort {
    List<Transacao> findByCartaoNumeroAndMes(String numeroCartao, Integer year, Integer monthValue);
}
