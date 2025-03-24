package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.YearMonth;

@Tag(name = "Cartões", description = "Gerenciamento de cartões de crédito")
public interface CartaoControllerSwagger {


    @Operation(summary = "Cadastrar um novo cartão", description = "Gera um cartão de crédito para um cliente.")
    CartaoResponse cadastrarCartao(@RequestBody CriarCartaoRequest dto);

    @Operation(summary = "Listar todos os cartões", description = "Retorna uma lista de cartões cadastrados.")
    PagedModel<CartaoResponse> listarCartoes(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(required = false) String cpf,
                                             @RequestParam(required = false) String numeroCartao
    );

    @Operation(summary = "Alterar limite do cartão", description = "Modifica o limite de crédito do cartão.")
    CartaoResponse alterarLimite(@PathVariable String numeroCartao, @RequestBody AlterarLimitRequest alterarLimitRequest);

    @Operation(summary = "Ativar cartão", description = "Altera o status do cartão para ATIVO.")
    CartaoResponse ativarCartao(@PathVariable String numeroCartao);

    @Operation(summary = "Cancelar cartão", description = "Altera o status do cartão para CANCELADO.")
    CartaoResponse cancelarCartao(@PathVariable String numeroCartao);

    @Operation(summary = "Bloquear cartão", description = "Altera o status do cartão para BLOQUEADO.")
    CartaoResponse bloquearCartao(@PathVariable String numeroCartao);

    @Operation(summary = "Ver fatura", description = "Retorna a fatura atual do cartão.")
    FaturaResponse visualizarFatura(
            @PathVariable String numeroCartao,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesAno);



}
