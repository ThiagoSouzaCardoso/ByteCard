package com.bytecard.adapter.out.persistence.relatorio.repository;

import com.bytecard.adapter.out.persistence.relatorio.entity.GastoCategoriaView;
import com.bytecard.adapter.out.persistence.relatorio.entity.GastoCategoriaViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GastoCategoriaViewRepository extends JpaRepository<GastoCategoriaView, GastoCategoriaViewId> {

    List<GastoCategoriaView> findByIdCartaoIdAndIdAnoAndIdMesOrderByIdCategoria(
            Long cartaoId, Integer ano, Integer mes
    );
}

