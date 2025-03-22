package com.bytecard.adapter.out.persistence.transacao.repository;

import com.bytecard.adapter.in.web.transacao.outputs.GastoPorCategoriaResponse;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    @Query("""
    SELECT new com.bytecard.adapter.in.web.transacao.outputs.GastoPorCategoriaResponse(t.categoria, SUM(t.valor))
    FROM TransacaoEntity t
    WHERE t.cartao.id = :cartaoId
      AND YEAR(t.dataHora) = :ano
      AND MONTH(t.dataHora) = :mes
    GROUP BY t.categoria
    ORDER BY t.categoria
""")
    List<GastoPorCategoriaResponse> somarGastosPorCategoriaNoMes(
            @Param("cartaoId") Long cartaoId,
            @Param("ano") int ano,
            @Param("mes") int mes
    );
}