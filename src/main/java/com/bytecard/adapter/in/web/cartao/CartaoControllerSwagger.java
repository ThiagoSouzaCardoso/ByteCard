package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Cartões", description = "Gerenciamento de cartões de crédito")
public interface CartaoControllerSwagger {


    @Operation(summary = "Cadastrar um novo cartão", description = "Gera um cartão de crédito para um cliente.")
    CartaoResponse cadastrarCartao(@RequestBody CriarCartaoRequest dto);

    @Operation(summary = "Listar todos os cartões", description = "Retorna uma lista de cartões cadastrados.")
    List<CartaoResponse> listarCartoes();

    @Operation(summary = "Alterar limite do cartão", description = "Modifica o limite de crédito do cartão.")
    CartaoResponse alterarLimite(@PathVariable Long id, @RequestBody BigDecimal novoLimite);

    @Operation(summary = "Ativar cartão", description = "Altera o status do cartão para ATIVO.")
    CartaoResponse ativarCartao(@PathVariable Long id);

    @Operation(summary = "Cancelar cartão", description = "Altera o status do cartão para CANCELADO.")
    CartaoResponse cancelarCartao(@PathVariable Long id);

    @Operation(summary = "Bloquear cartão", description = "Altera o status do cartão para BLOQUEADO.")
    CartaoResponse bloquearCartao(@PathVariable Long id);

    @Operation(summary = "Ver fatura", description = "Retorna a fatura atual do cartão.")
    EntityModel<String> verFatura(@PathVariable Long id);



}
