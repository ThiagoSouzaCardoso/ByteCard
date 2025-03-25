package com.bytecard.adapter.out.persistence.transacao.repository;

import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    @Query("""
    SELECT t
    FROM TransacaoEntity t
    WHERE t.cartao.id = :cartaoId
      AND YEAR(t.dataHora) = :ano
      AND MONTH(t.dataHora) = :mes
    ORDER BY t.categoria, t.dataHora
""")
    List<TransacaoEntity> buscarTransacoesPorCartaoEMes(
            @Param("cartaoId") Long cartaoId,
            @Param("ano") int ano,
            @Param("mes") int mes
    );


    @Query("""
    SELECT t FROM TransacaoEntity t
    WHERE t.cartao.numero = :numero
    AND YEAR(t.dataHora) = :ano
    AND MONTH(t.dataHora) = :mes
""")
    List<TransacaoEntity> findByCartaoNumeroAndMes(
            @Param("numero")  String numero,
            @Param("ano") int ano,
            @Param("mes") int mes
    );
}