package com.bytecard.adapter.out.persistence.cartao.repository;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {
}