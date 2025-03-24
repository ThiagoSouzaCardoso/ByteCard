package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.transacao.inputs.RelatorioGastosRequest;
import com.bytecard.adapter.in.web.transacao.outputs.RelatorioGastosResponse;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.model.GastoCategoria;
import com.bytecard.domain.model.RelatorioGastos;
import com.bytecard.domain.port.in.relatorio.RelatorioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.bytecard.domain.model.CategoriaTransacao.ALIMENTACAO;
import static com.bytecard.domain.model.CategoriaTransacao.CASA;
import static com.bytecard.domain.model.CategoriaTransacao.EDUCACAO;
import static com.bytecard.domain.model.CategoriaTransacao.SAUDE;
import static com.bytecard.domain.model.CategoriaTransacao.TRANSPORTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RelatorioControllerTest {

    @Mock
    private RelatorioUseCase relatorioUseCase;

    @InjectMocks
    private RelatorioController controller;

    @Test
    void deveGerarRelatorioDeGastosComSucesso() {
        var request = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2024, 12));

        var relatorio = new RelatorioGastos(
                "1234567812345678",
                "12",
                List.of(
                        new GastoCategoria(ALIMENTACAO, BigDecimal.valueOf(100.00)),
                        new GastoCategoria(SAUDE, BigDecimal.valueOf(150.00))
                ),
                BigDecimal.valueOf(250.00)
        );

        when(relatorioUseCase.gerarRelatorioPorCategoria(any())).thenReturn(relatorio);

        RelatorioGastosResponse response = controller.relatorioDeGastos(request);

        assertThat(response.cartaoNumero()).isEqualTo("1234567812345678");
        assertThat(response.mes()).isEqualTo("12");
        assertThat(response.gastos()).hasSize(2);
        assertThat(response.valorTotalGasto()).isEqualTo(BigDecimal.valueOf(250.00));
    }

    @Test
    void deveLancarExcecaoQuandoCartaoNaoExistir() {
        var request = new RelatorioGastosRequest("0000000000000000", YearMonth.of(2024, 12));

        when(relatorioUseCase.gerarRelatorioPorCategoria(any()))
                .thenThrow(new CartaoNotFoundException("Cartão não encontrado"));

        assertThatThrownBy(() -> controller.relatorioDeGastos(request))
                .isInstanceOf(CartaoNotFoundException.class)
                .hasMessage("Cartão não encontrado");
    }

    @Test
    void deveLancarExcecaoQuandoNaoHouverComprasNoPeriodo() {
        var request = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2030, 1));

        when(relatorioUseCase.gerarRelatorioPorCategoria(any()))
                .thenThrow(new RelatorioEmptyException("Nenhuma compra realizada no período."));

        assertThatThrownBy(() -> controller.relatorioDeGastos(request))
                .isInstanceOf(RelatorioEmptyException.class)
                .hasMessage("Nenhuma compra realizada no período.");
    }

    @Test
    void deveGerarRelatorioComGastosVaziosEValorTotalZero() {
        var request = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2024, 12));

        var relatorio = new RelatorioGastos(
                "1234567812345678",
                "12",
                List.of(),
                BigDecimal.ZERO
        );

        when(relatorioUseCase.gerarRelatorioPorCategoria(any())).thenReturn(relatorio);

        RelatorioGastosResponse response = controller.relatorioDeGastos(request);

        assertThat(response.gastos()).isEmpty();
        assertThat(response.valorTotalGasto()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deveMapearCategoriasCorretamenteNaResposta() {
        var request = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2024, 12));

        var relatorio = new RelatorioGastos(
                "1234567812345678",
                "12",
                List.of(
                        new GastoCategoria(CASA, BigDecimal.valueOf(90)),
                        new GastoCategoria(EDUCACAO, BigDecimal.valueOf(300)),
                        new GastoCategoria(TRANSPORTE, BigDecimal.valueOf(45))
                ),
                BigDecimal.valueOf(435)
        );

        when(relatorioUseCase.gerarRelatorioPorCategoria(any())).thenReturn(relatorio);

        var response = controller.relatorioDeGastos(request);

        assertThat(response.gastos())
                .extracting("categoria")
                .containsExactly(CASA, EDUCACAO, TRANSPORTE);
    }

    @Test
    void deveChamarUseCaseComNumeroECategoriaCorretos() {
        var request = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2024, 12));

        var relatorio = new RelatorioGastos("1234567812345678", "12", List.of(), BigDecimal.ZERO);
        when(relatorioUseCase.gerarRelatorioPorCategoria(any())).thenReturn(relatorio);

        controller.relatorioDeGastos(request);

        verify(relatorioUseCase).gerarRelatorioPorCategoria(argThat(model ->
                model.numeroCartao().equals("1234567812345678")
                        && model.mesAno().equals(YearMonth.of(2024, 12))
        ));
    }

}


