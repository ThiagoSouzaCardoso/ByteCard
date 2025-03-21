package com.bytecard.adapter.out.persistence.transacao.entity;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valor;

    private OffsetDateTime dataHora;

    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartao_id")
    private CartaoEntity cartao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaTransacao categoria;

    @PrePersist
    protected void onCreate() {
        dataHora = OffsetDateTime.now();
    }
}