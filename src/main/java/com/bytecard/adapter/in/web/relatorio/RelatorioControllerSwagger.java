package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.relatorio.inputs.RelatorioGastosRequest;
import com.bytecard.adapter.in.web.transacao.outputs.RelatorioGastosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Relatórios", description = "Relatórios financeiros e de consumo por cartão")
public interface RelatorioControllerSwagger {

    @Operation(
            summary = "Relatório de gastos por categoria",
            description = """
            Retorna os gastos agrupados por categoria para um cartão em determinado mês.
            Informe o número do cartão e o mês/ano no formato yyyy-MM.
        """,
            requestBody = @RequestBody(
                    required = true,
                    description = "Dados necessários para gerar o relatório",
                    content = @Content(
                            schema = @Schema(implementation = RelatorioGastosRequest.class),
                            examples = @ExampleObject(value = """
                    {
                      "numeroCartao": "1234567812345678",
                      "mesAno": "2024-12"
                    }
                """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Relatório gerado com sucesso",
                            content = @Content(schema = @Schema(implementation = RelatorioGastosResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cartão não encontrado ou sem transações no período"
                    )
            }
    )
    RelatorioGastosResponse relatorioDeGastos(@RequestBody RelatorioGastosRequest request);
}
