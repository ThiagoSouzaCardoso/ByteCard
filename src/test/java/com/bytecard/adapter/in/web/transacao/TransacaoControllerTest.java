package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoHateaosAssembler;
import com.bytecard.adapter.in.web.transacao.outputs.TransacaoResponse;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.transacao.TransacaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {


    @Mock
    private TransacaoHateaosAssembler transacaoHateaosAssembler;

    @Mock
    private TransacaoUseCase transacaoUseCase;

    @InjectMocks
    private TransacaoController controller;

    private CriarCompraRequest request;

    @BeforeEach
    void setUp() {

        this.request  = CriarCompraRequest.builder()
                .cartaoNumero("1234567812345678")
                .estabelecimento("Restaurante Central")
                .valor(BigDecimal.valueOf(150.00))
                .categoria(CategoriaTransacao.ALIMENTACAO)
                .build();
    }

    @Test
    void deveRegistrarCompraComSucesso() {
        Transacao transacao = request.toModel();
        Transacao transacaoCriada = transacao.toBuilder().id(1L).build();

        TransacaoResponse responseEsperada = TransacaoResponse.builder()
                .id(1L)
                .valor(BigDecimal.valueOf(150.00))
                .categoria(CategoriaTransacao.ALIMENTACAO)
                .estabelecimento("Restaurante Central")
                .build();
        responseEsperada.add(linkTo(TransacaoController.class).withSelfRel()); // simula link

        when(transacaoUseCase.registrarCompra(any())).thenReturn(transacaoCriada);
        when(transacaoHateaosAssembler.toModel(transacaoCriada)).thenReturn(responseEsperada);

        TransacaoResponse response = controller.registrarCompra(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getValor()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getCategoria()).isEqualTo(CategoriaTransacao.ALIMENTACAO);
        assertThat(response.getEstabelecimento()).isEqualTo("Restaurante Central");

        assertThat(response.getLinks()).isNotEmpty();
        assertThat(response.getLink("self")).isPresent();

        verify(transacaoUseCase).registrarCompra(any());
        verify(transacaoHateaosAssembler).toModel(transacaoCriada);
    }


    @Test
    void deveLancarExcecaoQuandoCartaoNaoForEncontrado() {
        when(transacaoUseCase.registrarCompra(any()))
                .thenThrow(new CartaoNotFoundException("Cartão não encontrado"));

        assertThatThrownBy(() -> controller.registrarCompra(request))
                .isInstanceOf(CartaoNotFoundException.class)
                .hasMessage("Cartão não encontrado");

        verify(transacaoUseCase).registrarCompra(any());
        verifyNoInteractions(transacaoHateaosAssembler);
    }
}

