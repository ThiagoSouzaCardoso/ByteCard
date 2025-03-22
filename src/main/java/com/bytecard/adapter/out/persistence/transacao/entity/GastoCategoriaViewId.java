package com.bytecard.adapter.out.persistence.transacao.entity;

import com.bytecard.domain.model.CategoriaTransacao;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class GastoCategoriaViewId implements Serializable {


    @Column(name = "cartao_id")
    private Long cartaoId;

    private Integer ano;
    private Integer mes;

    @Enumerated(EnumType.STRING)
    private CategoriaTransacao categoria;

    public GastoCategoriaViewId() {}

    public GastoCategoriaViewId(Long cartaoId, Integer ano, Integer mes, CategoriaTransacao categoria) {
        this.cartaoId = cartaoId;
        this.ano = ano;
        this.mes = mes;
        this.categoria = categoria;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GastoCategoriaViewId)) return false;
        GastoCategoriaViewId that = (GastoCategoriaViewId) o;
        return Objects.equals(cartaoId, that.cartaoId)
                && Objects.equals(ano, that.ano)
                && Objects.equals(mes, that.mes)
                && categoria == that.categoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartaoId, ano, mes, categoria);
    }


}
