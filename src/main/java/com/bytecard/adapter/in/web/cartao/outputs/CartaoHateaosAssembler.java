package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.adapter.in.web.cartao.CartaoController;
import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.domain.model.Cartao;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartaoHateaosAssembler extends RepresentationModelAssemblerSupport<Cartao,CartaoResponse > {


    public CartaoHateaosAssembler() {
        super(CartaoController.class, CartaoResponse.class);
    }


    @Override
    public CartaoResponse toModel(Cartao cartao) {
        CartaoResponse cartaoResponse = CartaoResponse.builder()
                .id(cartao.getId())
                .numero(cartao.getNumero())
                .cliente(cartao.getCliente().nome())
                .limite(cartao.getLimite())
                .validade(formatarValidade(cartao.getValidade()))
                .status(cartao.getStatus())
                .build();


        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .alterarLimite(cartao.getNumero(), new AlterarLimitRequest(cartao.getLimite()))).withRel("alterar-limite"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .visualizarFatura(cartao.getNumero(),YearMonth.now())).withRel("ver-fatura"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .ativarCartao(cartao.getNumero())).withRel("ativar"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .cancelarCartao(cartao.getNumero())).withRel("cancelar"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .bloquearCartao(cartao.getNumero())).withRel("bloquear"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .listarCartoes(0,10,cartao.getCliente().cpf(),cartao.getNumero())).withRel("listar-cartoes"));

        return cartaoResponse;
    }


    private String formatarValidade(YearMonth validade) {
        return String.format("%02d/%04d", validade.getMonthValue(), validade.getYear());
    }
}
