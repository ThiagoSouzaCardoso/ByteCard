package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Tag(name = "Cartões", description = "Gerenciamento de cartões de crédito")
public interface CartaoControllerSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Cadastrar um novo cartão", description = "Gera um cartão de crédito para um cliente com um limite inicial definido.")
    CartaoResponse cadastrarCartao(
            @Valid @RequestBody CriarCartaoRequest dto
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Listar todos os cartões", description = "Retorna uma lista paginada de cartões cadastrados, com filtros opcionais por CPF e número do cartão.")
    PagedModel<CartaoResponse> listarCartoes(
            @Parameter(description = "Número da página (0 ou maior)", example = "0")
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "A página deve ser 0 ou maior") Integer pageNo,

            @Parameter(description = "Quantidade de registros por página (maior que 0)", example = "10")
            @RequestParam(defaultValue = "10") @Positive(message = "O tamanho da página deve ser maior que 0") Integer pageSize,

            @Parameter(description = "CPF do cliente para filtragem", example = "12345678900")
            @RequestParam(required = false) String cpf,

            @Parameter(description = "Número do cartão para filtragem", example = "1234567812345678")
            @RequestParam(required = false) String numeroCartao
    );

    @PatchMapping("/{numero}/alterar-limite")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Alterar limite do cartão", description = "Modifica o limite de crédito de um cartão específico.")
    CartaoResponse alterarLimite(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero,

            @Valid @RequestBody AlterarLimitRequest alterarLimitRequest
    );

    @PatchMapping("/{numero}/ativar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Ativar cartão", description = "Ativa o cartão, tornando-o utilizável.")
    CartaoResponse ativarCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @PatchMapping("/{numero}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Cancelar cartão", description = "Cancela o cartão, tornando-o inutilizável permanentemente.")
    CartaoResponse cancelarCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @PatchMapping("/{numero}/bloquear")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Bloquear cartão", description = "Bloqueia o cartão temporariamente.")
    CartaoResponse bloquearCartao(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable String numero
    );

    @GetMapping("/{numero}/fatura")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Visualizar fatura", description = "Retorna a fatura de um cartão para o mês/ano especificado (formato: yyyy-MM).")
    FaturaResponse visualizarFatura(
            @Parameter(description = "Número do cartão", example = "1234567812345678")
            @PathVariable("numero") String numeroCartao,

            @Parameter(description = "Mês e ano da fatura (formato: yyyy-MM)", example = "2024-12")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesAno
    );
}
