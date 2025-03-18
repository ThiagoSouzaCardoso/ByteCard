package com.bytecard.adapter.out.persistence.transacao.repository;

import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {
}