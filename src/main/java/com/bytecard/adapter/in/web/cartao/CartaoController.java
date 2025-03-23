package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.YearMonth;

@RestController
@RequestMapping("/cartoes")
public class CartaoController implements CartaoControllerSwagger{

    private final CartaoUseCase cartaoUseCase;
    private final CartaoHateaosAssembler cartaoHateaosAssembler;
    private final PagedResourcesAssembler<Cartao> pagedResourcesAssembler;

    public CartaoController(CartaoUseCase cartaoUseCase,
                            CartaoHateaosAssembler cartaoHateaosAssembler,
                            PagedResourcesAssembler<Cartao> pagedResourcesAssembler) {
        this.cartaoUseCase = cartaoUseCase;
        this.cartaoHateaosAssembler = cartaoHateaosAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse cadastrarCartao(@Valid @RequestBody CriarCartaoRequest request) {

        var cartao = request.toModel();

        var cartaoCriado = cartaoUseCase.register(cartao);
        return cartaoHateaosAssembler.toModel(cartaoCriado);
    }

    @GetMapping
   @ResponseStatus(HttpStatus.OK)
   @PreAuthorize("hasRole('GERENTE')")
   @Override
   public PagedModel<CartaoResponse> listarCartoes(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) String cpf,
                                                   @RequestParam(required = false) String numero) {

        Page<Cartao> cartoes = cartaoUseCase.getAllCartoes(pageNo,pageSize,cpf,numero);

       return pagedResourcesAssembler.toModel(cartoes, cartaoHateaosAssembler);

   }

    @PatchMapping("/{numero}/alterar-limite")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse alterarLimite(@PathVariable String numero, @RequestBody BigDecimal novoLimite) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarLimit(novoLimite,numero));
    }

    @PatchMapping("/{numero}/ativar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse ativarCartao(@PathVariable String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero, StatusCartao.ATIVO));
    }

    @PatchMapping("/{numero}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse cancelarCartao(@PathVariable String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero,StatusCartao.CANCELADO));
    }

    @PatchMapping("/{numero}/bloquear")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    @Override
    public CartaoResponse bloquearCartao(@PathVariable String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero,StatusCartao.BLOQUEADO));
    }


    @GetMapping("/{numero}/fatura")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('GERENTE')")
    public FaturaResponse visualizarFatura(
            @PathVariable String numero,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesAno) {

        return FaturaResponse.from(cartaoUseCase.gerarFaturaPorNumero(numero, mesAno));
    }

}