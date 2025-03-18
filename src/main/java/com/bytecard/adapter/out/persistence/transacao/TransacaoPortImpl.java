package com.bytecard.adapter.out.persistence.transacao;

import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.port.out.transacao.TransacaoPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TransacaoPortImpl implements TransacaoPort {

    private TransacaoRepository cartaoRepository;


}