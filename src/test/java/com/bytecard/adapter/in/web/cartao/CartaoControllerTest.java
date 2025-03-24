package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CartaoControllerTest {

    @Mock
    private CartaoUseCase cartaoUseCase;

    @Mock
    private CartaoHateaosAssembler assembler;

    @Mock
    private PagedResourcesAssembler<Cartao> pagedResourcesAssembler;

    @InjectMocks
    private CartaoController controller;

    @Test
    void deveCadastrarCartaoComSucesso() {
        CriarCartaoRequest request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "cliente@email.com");
        Cartao cartao = request.toModel();
        Cartao cartaoSalvo = cartao.toBuilder().id(1L).build();

        when(cartaoUseCase.register(any())).thenReturn(cartaoSalvo);
        when(assembler.toModel(cartaoSalvo)).thenReturn(mock(CartaoResponse.class));

        controller.cadastrarCartao(request);

        verify(cartaoUseCase).register(any());
        verify(assembler).toModel(cartaoSalvo);
    }

    @Test
    void deveAlterarLimiteComSucesso() {
        when(cartaoUseCase.alterarLimit(any(), any())).thenReturn(mock(Cartao.class));
        when(assembler.toModel(any())).thenReturn(mock(CartaoResponse.class));

        controller.alterarLimite("1234", BigDecimal.valueOf(500));

        verify(cartaoUseCase).alterarLimit(BigDecimal.valueOf(500), "1234");
    }

}

