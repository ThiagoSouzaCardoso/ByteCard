package com.bytecard.adapter.out.persistence.cartao.entity;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.converter.YearMonthConverter;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 16)
    private String numero;

    @Column(unique = true, nullable = false, length = 16)
    private String cvv;

    @Convert(converter = YearMonthConverter.class)
    @Column(nullable = false, length = 7)
    private YearMonth validade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal limite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCartao  status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TransacaoEntity> transacoes;
}