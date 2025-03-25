package com.bytecard.adapter.out.persistence.relatorio;

import com.bytecard.adapter.out.persistence.relatorio.entity.GastoCategoriaView;
import com.bytecard.adapter.out.persistence.relatorio.entity.GastoCategoriaViewId;
import com.bytecard.adapter.out.persistence.relatorio.repository.GastoCategoriaViewRepository;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.GastoCategoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioPortImplTest {

    @Mock
    private GastoCategoriaViewRepository repository;

    @InjectMocks
    private RelatorioPortImpl relatorioPort;

    @Test
    void deveRetornarGastosPorCategoriaComSucesso() {
        GastoCategoriaViewId id = new GastoCategoriaViewId(1L, 2024, 3, CategoriaTransacao.ALIMENTACAO);
        GastoCategoriaView view = new GastoCategoriaView();
        view.setId(id);
        view.setTotal(new BigDecimal("150.00"));

        when(repository.findByIdCartaoIdAndIdAnoAndIdMesOrderByIdCategoria(1L, 2024, 3))
                .thenReturn(List.of(view));

        List<GastoCategoria> resultado = relatorioPort.getSomatorioGastosPorCategoriaNoMes(1L, 2024, 3);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).categoria()).isEqualTo(CategoriaTransacao.ALIMENTACAO);
        assertThat(resultado.get(0).total()).isEqualTo(new BigDecimal("150.00"));
    }
}

