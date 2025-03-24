package com.bytecard.adapter.out.persistence.relatorio.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "vw_gasto_categoria")
@Getter
@Setter
public class GastoCategoriaView {

    @EmbeddedId
    private GastoCategoriaViewId id;

    private BigDecimal total;

}

