package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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


    @Override
    public CartaoResponse cadastrarCartao(CriarCartaoRequest request) {

        var cartao = request.toModel();

        var cartaoCriado = cartaoUseCase.register(cartao);
        return cartaoHateaosAssembler.toModel(cartaoCriado);
    }


   @Override
   public PagedModel<CartaoResponse> listarCartoes(Integer pageNo,
                                                   Integer pageSize,
                                                   String cpf,
                                                   String numero) {

        Page<Cartao> cartoes = cartaoUseCase.getAllCartoes(pageNo,pageSize,cpf,numero);

       return pagedResourcesAssembler.toModel(cartoes, cartaoHateaosAssembler);

   }


    @Override
    public CartaoResponse alterarLimite(String numero,
                                        AlterarLimitRequest alterarLimitRequest) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarLimit(alterarLimitRequest.novoLimite(),numero));
    }


    @Override
    public CartaoResponse ativarCartao(String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero, StatusCartao.ATIVO));
    }


    @Override
    public CartaoResponse cancelarCartao(String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero,StatusCartao.CANCELADO));
    }


    @Override
    public CartaoResponse bloquearCartao(String numero) {
        return cartaoHateaosAssembler.toModel(cartaoUseCase.alterarStatusCartao(numero,StatusCartao.BLOQUEADO));
    }



    @Override
    public FaturaResponse visualizarFatura(
            String numero,
            YearMonth mesAno) {

        return FaturaResponse.from(cartaoUseCase.gerarFaturaPorNumero(numero, mesAno));
    }

}