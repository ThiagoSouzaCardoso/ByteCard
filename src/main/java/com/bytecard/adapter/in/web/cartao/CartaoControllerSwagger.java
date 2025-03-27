package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.YearMonth;

@Tag(name = "Cartões", description = "Endpoints para gerenciamento de cartões de crédito")
@SecurityRequirement(name = "Bearer Token")
@RequestMapping("/cartoes")
public interface CartaoControllerSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar um novo cartão",
            description = "Cria um novo cartão de crédito para um cliente com limite inicial definido.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
            }
    )
    CartaoResponse cadastrarCartao(
            @Valid @RequestBody CriarCartaoRequest dto
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Listar cartões",
            description = "Retorna uma lista paginada de cartões, com filtros opcionais por CPF e número do cartão."
    )
    PagedModel<CartaoResponse> listarCartoes(
            @Parameter(description = "Número da página (começando em 0)", example = "0")
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer pagina,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(defaultValue = "10") @Positive Integer tamanhoPagina,

            @Parameter(description = "CPF do cliente para filtrar", example = "12345678900")
            @RequestParam(required = false) String cpf,

            @Parameter(description = "Número do cartão para filtrar", example = "1234567812345678")
            @RequestParam(required = false) String numeroCartao
    );

    @PatchMapping("/{numero}/alterar-limite")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Alterar limite do cartão",
            description = "Atualiza o limite de crédito disponível para o cartão informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limite alterado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
            }
    )
    CartaoResponse alterarLimite(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero,

            @Valid @RequestBody AlterarLimitRequest alterarLimitRequest
    );

    @PatchMapping("/{numero}/ativar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Ativar cartão",
            description = "Ativa o cartão para permitir transações.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cartão ativado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
            }
    )
    CartaoResponse ativarCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @PatchMapping("/{numero}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Cancelar cartão",
            description = "Cancela o cartão permanentemente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cartão cancelado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
            }
    )
    CartaoResponse cancelarCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @PatchMapping("/{numero}/bloquear")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Bloquear cartão",
            description = "Bloqueia temporariamente o cartão.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cartão bloqueado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content)
            }
    )
    CartaoResponse bloquearCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @GetMapping("/{numero}/fatura")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Visualizar fatura",
            description = "Retorna os gastos e lançamentos do cartão em um determinado mês/ano.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fatura retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cartão ou fatura não encontrados", content = @Content)
            }
    )
    FaturaResponse visualizarFatura(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable("numero") String numeroCartao,

            @Parameter(description = "Mês e ano da fatura no formato yyyy-MM", example = "2024-12")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesAno
    );
}
