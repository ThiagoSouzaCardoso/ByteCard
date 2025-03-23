package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.transacao.inputs.RelatorioGastosRequest;
import com.bytecard.adapter.in.web.transacao.outputs.RelatorioGastosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Relatórios", description = "Relatórios financeiros e de consumo por cartão")
public interface RelatorioControllerSwagger {

    @Operation(
            summary = "Relatório de gastos por categoria",
            description = """
            Retorna a soma dos gastos agrupados por categoria em um determinado mês para um cartão específico.
            A consulta deve ser feita informando o número do cartão e o mês/ano desejado.
            Se não houver transações no período, o sistema informará adequadamente.
        """)
    RelatorioGastosResponse relatorioDeGastos(@RequestBody RelatorioGastosRequest request);

}
