package com.bytecard.domain.port.in.transacao;

import com.bytecard.domain.model.Transacao;

public interface TransacaoUseCase {
    Transacao registrarCompra(Transacao dto);
}
