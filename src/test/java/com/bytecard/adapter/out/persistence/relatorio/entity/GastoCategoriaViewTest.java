package com.bytecard.adapter.out.persistence.relatorio.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.bytecard.domain.model.CategoriaTransacao.ALIMENTACAO;
import static com.bytecard.domain.model.CategoriaTransacao.LAZER;
import static com.bytecard.domain.model.CategoriaTransacao.TRANSPORTE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GastoCategoriaViewId")
class GastoCategoriaViewIdTest {

    @Test
    @DisplayName("Deve considerar objetos iguais quando possuem mesmos campos")
    void deveRetornarTrueParaObjetosIguais() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);
        var id2 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);


        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar objetos diferentes quando campos são diferentes")
    void deveRetornarFalseParaObjetosDiferentes() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);
        var id2 = new GastoCategoriaViewId(1L,2025,3, TRANSPORTE);


        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false se comparado com outro tipo")
    void deveRetornarFalseParaTipoDiferente() {
        var id = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);
        assertThat(id.equals("string aleatória")).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true ao comparar com ele mesmo")
    void deveRetornarTrueAoCompararComEleMesmo() {
        var id = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        assertThat(id).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveRetornarFalseAoCompararComNull() {
        var id = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        assertThat(id.equals(null)).isFalse();
    }



    @Test
    @DisplayName("Deve retornar false se cartaoId for diferente")
    void deveRetornarFalseQuandoCartaoIdDiferente() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        var id2 = new GastoCategoriaViewId(2L,2025,3, ALIMENTACAO);

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Deve retornar false se ano for diferente")
    void deveRetornarFalseQuandoAnoDiferente() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        var id2 = new GastoCategoriaViewId(1L,2024,3, ALIMENTACAO);

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Deve retornar false se mês for diferente")
    void deveRetornarFalseQuandoMesDiferente() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        var id2 = new GastoCategoriaViewId(1L,2025,2, ALIMENTACAO);

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Deve retornar false se categoria for diferente")
    void deveRetornarFalseQuandoCategoriaDiferente() {
        var id1 = new GastoCategoriaViewId(1L,2025,3, ALIMENTACAO);

        var id2 = new GastoCategoriaViewId(1L,2025,3, LAZER);
        assertThat(id1).isNotEqualTo(id2);
    }

}
