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
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@PreAuthorize("hasRole('GERENTE')")
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
   public PagedModel<CartaoResponse> listarCartoes(Integer pagina,
                                                   Integer tamanhoPagina,
                                                   String cpf,
                                                   String numero) {

        Page<Cartao> cartoes = cartaoUseCase.getAllCartoes(pagina,tamanhoPagina,cpf,numero);

       return pagedResourcesAssembler.toModel(cartoes, cartaoHateaosAssembler);

   }


    @Override
    public CartaoResponse alterarLimite(String numero,
                                       @Valid AlterarLimitRequest alterarLimitRequest) {
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