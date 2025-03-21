package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoHateaosAssembler;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoResponse;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
public class TransacaoController implements TransacaoControllerSwagger {

    private final TransacaoHateaosAssembler transacaoHateaosAssembler;
    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoHateaosAssembler transacaoHateaosAssembler, TransacaoService transacaoService) {
        this.transacaoHateaosAssembler = transacaoHateaosAssembler;
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse registrarCompra(@RequestBody CriarCompraRequest dto) {
        Transacao transacaoCriada = transacaoService.registrarCompra(dto.toModel());
        return transacaoHateaosAssembler.toModel(transacaoCriada);
    }


}
