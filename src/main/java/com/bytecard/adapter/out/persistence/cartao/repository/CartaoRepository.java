package com.bytecard.adapter.out.persistence.cartao.repository;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {

    @Query("SELECT c FROM CartaoEntity c JOIN c.cliente cli ORDER BY cli.nome ASC, c.numero ASC")
    List<CartaoEntity> findAllOrdered();

    @Query("SELECT c FROM CartaoEntity c JOIN c.cliente cli ORDER BY cli.nome ASC, c.numero ASC")
    Page<CartaoEntity> findAllOrdered(Pageable pageable);
}