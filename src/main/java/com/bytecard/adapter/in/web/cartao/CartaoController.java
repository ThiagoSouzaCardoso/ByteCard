package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoUseCase cartaoUseCase;
    private final CartaoHateaosAssembler cartaoHateaosAssembler;


    public CartaoController(CartaoUseCase cartaoUseCase, CartaoHateaosAssembler cartaoHateaosAssembler) {
        this.cartaoUseCase = cartaoUseCase;
        this.cartaoHateaosAssembler = cartaoHateaosAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('GERENTE')")
    public CartaoResponse cadastrarCartao(@RequestBody CriarCartaoRequest dto) {

        var cartao = Cartao.builder()
                .cliente(Cliente.builder()
                        .email(dto.email())
                        .build())
                        .limite(dto.limite()).build();

        var cartaoCriado = cartaoUseCase.register(cartao);

        return cartaoHateaosAssembler.toModel(cartaoCriado);
    }

   @GetMapping
   public CollectionModel<CartaoResponse> listarCartoes() {

        List<Cartao> cartoes = cartaoUseCase.getAllCartoes();

        return cartaoHateaosAssembler.toCollectionModel(cartoes);
   }


    @PostMapping("/{id}/alterar-limite")
    @ResponseStatus(HttpStatus.OK)
    public CartaoResponse alterarLimite(@PathVariable Long id, @RequestBody BigDecimal novoLimite) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarLimit(novoLimite,id));
    }

    @PostMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.OK)
    public CartaoResponse ativarCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"ATIVO"));
    }

    @PostMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.OK)

    public CartaoResponse cancelarCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"CANCELADO"));
    }

    @PostMapping("/{id}/bloquear")
    @ResponseStatus(HttpStatus.OK)
    public CartaoResponse bloquearCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"BLOQUEADO"));
    }


    @GetMapping("/{id}/ver-fatura")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<String> verFatura(@PathVariable Long id) {
        return EntityModel.of("Fatura do cartão " + id);
    }

}