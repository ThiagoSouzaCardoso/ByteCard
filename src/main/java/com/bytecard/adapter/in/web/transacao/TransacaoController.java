package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoResponse;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
public class TransacaoController {


    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar uma nova compra", description = "Registra uma compra vinculada a um cartão escolhido.")
    public TransacaoResponse registrarCompra(@RequestBody CriarCompraRequest dto) {
        Transacao transacaoCriada = transacaoService.registrarCompra(new Transacao());
        return new TransacaoResponse();
    }


}
