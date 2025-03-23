package com.bytecard.adapter.out.persistence.cartao.repository;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {

    @Query("SELECT c FROM CartaoEntity c JOIN c.cliente cli ORDER BY cli.nome ASC, c.numero ASC")
    List<CartaoEntity> findAllOrdered();

    @Query("""
    SELECT c FROM CartaoEntity c
    JOIN c.cliente cli
    WHERE (:cpf IS NULL OR cli.cpf = :cpf)
      AND (:numero IS NULL OR c.numero LIKE %:numero%)
    ORDER BY cli.nome ASC, c.numero ASC
""")
    Page<CartaoEntity> findAllOrdered(
            @Param("cpf") String cpf,
            @Param("numero") String numero,
            Pageable pageable);

    Optional<CartaoEntity> findByNumero(String numero);


}