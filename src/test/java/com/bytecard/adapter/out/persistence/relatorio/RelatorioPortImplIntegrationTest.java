package com.bytecard.adapter.out.persistence.relatorio;

import com.bytecard.adapter.out.persistence.relatorio.repository.GastoCategoriaViewRepository;
import com.bytecard.domain.model.CategoriaTransacao;
import com.bytecard.domain.model.GastoCategoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(RelatorioPortImpl.class)
class RelatorioPortImplIntegrationTest {

    @Autowired
    private GastoCategoriaViewRepository repository;

    @Autowired
    private RelatorioPortImpl relatorioPort;


    @Test
    void deveConsultarGastosAgrupadosPorCategoria() {
        List<GastoCategoria> resultado = relatorioPort.getSomatorioGastosPorCategoriaNoMes(1L,YearMonth.now().getYear() , YearMonth.now().getMonthValue());

        assertThat(resultado).hasSize(7);
        assertThat(resultado.get(0).categoria()).isEqualTo(CategoriaTransacao.ALIMENTACAO);
        assertThat(resultado.get(0).total()).isEqualTo(new BigDecimal("120.00"));
        assertThat(resultado.get(1).categoria()).isEqualTo(CategoriaTransacao.CASA);
        assertThat(resultado.get(1).total()).isEqualTo(new BigDecimal("220.00"));
        assertThat(resultado.get(2).categoria()).isEqualTo(CategoriaTransacao.EDUCACAO);
        assertThat(resultado.get(2).total()).isEqualTo(new BigDecimal("300.00"));
        assertThat(resultado.get(3).categoria()).isEqualTo(CategoriaTransacao.LAZER);
        assertThat(resultado.get(3).total()).isEqualTo(new BigDecimal("45.50"));
        assertThat(resultado.get(4).categoria()).isEqualTo(CategoriaTransacao.OUTROS);
        assertThat(resultado.get(4).total()).isEqualTo(new BigDecimal("55.75"));
        assertThat(resultado.get(5).categoria()).isEqualTo(CategoriaTransacao.SAUDE);
        assertThat(resultado.get(5).total()).isEqualTo(new BigDecimal("89.90"));
        assertThat(resultado.get(6).categoria()).isEqualTo(CategoriaTransacao.TRANSPORTE);
        assertThat(resultado.get(6).total()).isEqualTo(new BigDecimal("15.00"));

    }

}

