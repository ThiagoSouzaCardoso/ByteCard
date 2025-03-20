package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.adapter.in.web.cartao.CartaoController;
import com.bytecard.domain.model.Cartao;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import java.time.YearMonth;

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
                .alterarLimite(cartao.getId(), cartao.getLimite())).withRel("alterar-limite"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .verFatura(cartao.getId())).withRel("ver-fatura"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .ativarCartao(cartao.getId())).withRel("ativar"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .cancelarCartao(cartao.getId())).withRel("cancelar"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .bloquearCartao(cartao.getId())).withRel("bloquear"));

        cartaoResponse.add(linkTo(methodOn(CartaoController.class)
                .listarCartoes()).withRel("listar-cartoes"));

        return cartaoResponse;
    }


    private String formatarValidade(YearMonth validade) {
        return String.format("%02d/%04d", validade.getMonthValue(), validade.getYear());
    }
}
