package com.bytecard.domain.port.out.transacao;

import com.bytecard.domain.model.Transacao;

public interface RegistrarTransacaoPort {

    Transacao registrar(Transacao novaTransacao);
}