package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController implements CartaoControllerSwagger{

    private final CartaoUseCase cartaoUseCase;
    private final CartaoHateaosAssembler cartaoHateaosAssembler;
    private final PagedResourcesAssembler<Cartao> pagedResourcesAssembler;

    public CartaoController(CartaoUseCase cartaoUseCase, CartaoHateaosAssembler cartaoHateaosAssembler, PagedResourcesAssembler<Cartao> pagedResourcesAssembler) {
        this.cartaoUseCase = cartaoUseCase;
        this.cartaoHateaosAssembler = cartaoHateaosAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse cadastrarCartao(@RequestBody CriarCartaoRequest request) {

        var cartao = request.toModel();

        var cartaoCriado = cartaoUseCase.register(cartao);
        return cartaoHateaosAssembler.toModel(cartaoCriado);
    }

    @GetMapping
   @ResponseStatus(HttpStatus.OK)
   @PreAuthorize("hasRole('GERENTE')")
   @Override
   public PagedModel<CartaoResponse> listarCartoes(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Cartao> cartoes = cartaoUseCase.getAllCartoes(pageNo,pageSize);

       return pagedResourcesAssembler.toModel(cartoes, cartaoHateaosAssembler);

   }

    @PatchMapping("/{id}/alterar-limite")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse alterarLimite(@PathVariable Long id, @RequestBody BigDecimal novoLimite) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarLimit(novoLimite,id));
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse ativarCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"ATIVO"));
    }

    @PatchMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse cancelarCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"CANCELADO"));
    }

    @PatchMapping("/{id}/bloquear")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse bloquearCartao(@PathVariable Long id) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(id,"BLOQUEADO"));
    }

    @GetMapping("/{id}/ver-fatura")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public EntityModel<String> verFatura(@PathVariable Long id) {
        return EntityModel.of("Fatura do cartão " + id);
    }

}