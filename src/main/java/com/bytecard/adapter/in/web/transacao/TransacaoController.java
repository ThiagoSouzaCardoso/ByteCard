package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoHateaosAssembler;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoResponse;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
@PreAuthorize("hasRole('GERENTE')")
public class TransacaoController implements TransacaoControllerSwagger {

    private final TransacaoHateaosAssembler transacaoHateaosAssembler;
    private final TransacaoUseCase transacaoUseCase;

    public TransacaoController(TransacaoHateaosAssembler transacaoHateaosAssembler, TransacaoUseCase transacaoUseCase) {
        this.transacaoHateaosAssembler = transacaoHateaosAssembler;
        this.transacaoUseCase = transacaoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse registrarCompra(@Valid @RequestBody CriarCompraRequest dto) {
        Transacao transacaoCriada = transacaoUseCase.registrarCompra(dto.toModel());
        return transacaoHateaosAssembler.toModel(transacaoCriada);
    }

}