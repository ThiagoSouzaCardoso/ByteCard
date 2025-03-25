package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoHateaosAssembler;
import com.bytecard.adapter.in.web.cartao.outputs.CartaoResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.model.Fatura;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartaoControllerTest {

    @Mock private CartaoUseCase cartaoUseCase;
    @Mock private CartaoHateaosAssembler assembler;
    @Mock private PagedResourcesAssembler<Cartao> pagedResourcesAssembler;

    @InjectMocks private CartaoController controller;

    @Test
    @DisplayName("Deve cadastrar cartão com sucesso")
    void deveCadastrarCartaoComSucesso() {
        CriarCartaoRequest request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "cliente@email.com");
        Cartao cartao = request.toModel();
        Cartao cartaoSalvo = cartao.toBuilder().id(1L).build();
        CartaoResponse responseEsperado = mock(CartaoResponse.class);

        when(cartaoUseCase.register(any())).thenReturn(cartaoSalvo);
        when(assembler.toModel(cartaoSalvo)).thenReturn(responseEsperado);

        CartaoResponse response = controller.cadastrarCartao(request);

        assertThat(response).isSameAs(responseEsperado);
        verify(cartaoUseCase).register(cartao);
        verify(assembler).toModel(cartaoSalvo);
    }

    @Test
    @DisplayName("Deve alterar limite com sucesso")
    void deveAlterarLimiteComSucesso() {
        Cartao cartao = mock(Cartao.class);
        CartaoResponse esperado = mock(CartaoResponse.class);
        BigDecimal novoLimite = BigDecimal.valueOf(500);
        String numero = "1234";

        when(cartaoUseCase.alterarLimit(novoLimite, numero)).thenReturn(cartao);
        when(assembler.toModel(cartao)).thenReturn(esperado);

        CartaoResponse response = controller.alterarLimite(numero, new AlterarLimitRequest(novoLimite));

        assertThat(response).isSameAs(esperado);
        verify(cartaoUseCase).alterarLimit(novoLimite, numero);
        verify(assembler).toModel(cartao);
    }

    @Test
    @DisplayName("Deve ativar cartão com sucesso")
    void deveAtivarCartaoComSucesso() {
        Cartao cartao = mock(Cartao.class);
        CartaoResponse esperado = mock(CartaoResponse.class);

        when(cartaoUseCase.alterarStatusCartao("1234", StatusCartao.ATIVO)).thenReturn(cartao);
        when(assembler.toModel(cartao)).thenReturn(esperado);

        CartaoResponse response = controller.ativarCartao("1234");

        assertThat(response).isSameAs(esperado);
        verify(cartaoUseCase).alterarStatusCartao("1234", StatusCartao.ATIVO);
    }

    @Test
    @DisplayName("Deve cancelar cartão com sucesso")
    void deveCancelarCartaoComSucesso() {
        Cartao cartao = mock(Cartao.class);
        CartaoResponse esperado = mock(CartaoResponse.class);

        when(cartaoUseCase.alterarStatusCartao("1234", StatusCartao.CANCELADO)).thenReturn(cartao);
        when(assembler.toModel(cartao)).thenReturn(esperado);

        CartaoResponse response = controller.cancelarCartao("1234");

        assertThat(response).isSameAs(esperado);
        verify(cartaoUseCase).alterarStatusCartao("1234", StatusCartao.CANCELADO);
    }

    @Test
    @DisplayName("Deve bloquear cartão com sucesso")
    void deveBloquearCartaoComSucesso() {
        Cartao cartao = mock(Cartao.class);
        CartaoResponse esperado = mock(CartaoResponse.class);

        when(cartaoUseCase.alterarStatusCartao("1234", StatusCartao.BLOQUEADO)).thenReturn(cartao);
        when(assembler.toModel(cartao)).thenReturn(esperado);

        CartaoResponse response = controller.bloquearCartao("1234");

        assertThat(response).isSameAs(esperado);
        verify(cartaoUseCase).alterarStatusCartao("1234", StatusCartao.BLOQUEADO);
    }

    @Test
    @DisplayName("Deve visualizar fatura com sucesso")
    void deveVisualizarFaturaComSucesso() {
        String numeroCartao = "1234567812345678";
        YearMonth mesAno = YearMonth.of(2024, 12);

        Cliente cliente = Cliente.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("123")
                .papel("ROLE_USER")
                .build();

        Cartao cartao = Cartao.builder()
                .numero(numeroCartao)
                .cliente(cliente)
                .limite(BigDecimal.valueOf(1000))
                .limiteUtilizado(BigDecimal.valueOf(200))
                .status(StatusCartao.ATIVO)
                .build();

        Fatura fatura = new Fatura(cliente, cartao, mesAno, BigDecimal.valueOf(200), List.of());
        FaturaResponse esperado = mock(FaturaResponse.class);

        when(cartaoUseCase.gerarFaturaPorNumero(numeroCartao, mesAno)).thenReturn(fatura);

        try (var mockedStatic = mockStatic(FaturaResponse.class)) {
            mockedStatic.when(() -> FaturaResponse.from(fatura)).thenReturn(esperado);

            FaturaResponse response = controller.visualizarFatura(numeroCartao, mesAno);

            assertThat(response).isSameAs(esperado);
            verify(cartaoUseCase).gerarFaturaPorNumero(numeroCartao, mesAno);
            mockedStatic.verify(() -> FaturaResponse.from(fatura));
        }
    }

    @Test
    @DisplayName("Deve listar cartões com sucesso")
    void deveListarCartoesComSucesso() {
        Page<Cartao> pagina = mock(Page.class);
        PagedModel<CartaoResponse> esperado = mock(PagedModel.class);

        when(cartaoUseCase.getAllCartoes(0, 10, null, null)).thenReturn(pagina);
        when(pagedResourcesAssembler.toModel(pagina, assembler)).thenReturn(esperado);

        PagedModel<CartaoResponse> response = controller.listarCartoes(0, 10, null, null);

        assertThat(response).isSameAs(esperado);
        verify(cartaoUseCase).getAllCartoes(0, 10, null, null);
        verify(pagedResourcesAssembler).toModel(pagina, assembler);
    }
}
