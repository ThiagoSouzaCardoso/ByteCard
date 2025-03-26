package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Compras", description = "Gerenciamento de compras (transações) vinculadas aos cartões")
public interface TransacaoControllerSwagger {

    @Operation(
            summary = "Registrar uma nova compra",
            description = "Registra uma nova transação vinculada a um cartão já existente."
    )
    TransacaoResponse registrarCompra(@RequestBody CriarCompraRequest dto);
}
