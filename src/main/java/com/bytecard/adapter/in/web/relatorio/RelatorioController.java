package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.transacao.inputs.RelatorioGastosRequest;
import com.bytecard.adapter.in.web.transacao.outputs.GastoPorCategoriaResponse;
import com.bytecard.adapter.in.web.transacao.outputs.RelatorioGastosResponse;
import com.bytecard.domain.port.in.relatorio.RelatorioUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController implements RelatorioControllerSwagger {

    private final RelatorioUseCase relatorioUseCase;

    public RelatorioController(RelatorioUseCase transacaoService) {
        this.relatorioUseCase = transacaoService;
    }

    @PostMapping
    public RelatorioGastosResponse relatorioDeGastos(@Valid @RequestBody RelatorioGastosRequest request) {

        var relatorio = relatorioUseCase.gerarRelatorioPorCategoria(request.toModel());
        return new RelatorioGastosResponse(
                relatorio.cartaoNumero(),
                relatorio.mes(),
                relatorio.gastos().stream()
                        .map(GastoPorCategoriaResponse::from)
                        .toList(),
                relatorio.valorTotalGasto()
        );
    }
}
