package com.bytecard.adapter.in.web.transacao.outputs;

import com.bytecard.adapter.in.web.transacao.TransacaoController;
import com.bytecard.adapter.in.web.relatorio.inputs.CriarCompraRequest;
import com.bytecard.domain.model.Transacao;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TransacaoHateaosAssembler extends RepresentationModelAssemblerSupport<Transacao, TransacaoResponse> {


    public TransacaoHateaosAssembler() {
        super(TransacaoController.class, TransacaoResponse.class);
    }

    @Override
    public TransacaoResponse toModel(Transacao model) {

        var transacaoResponse =TransacaoResponse.builder()
                .id(model.getId())
                .valor(model.getValor())
                .cartaoNumero(model.getCartao().getNumero())
                .categoria(model.getCategoria())
                .estabelecimento(model.getEstabelecimento())
                .dataHora(model.getData())
                .build();

        transacaoResponse.add(linkTo(methodOn(TransacaoController.class)
                .registrarCompra(CriarCompraRequest.builder().build())).withSelfRel());

        return transacaoResponse;
    }
}
