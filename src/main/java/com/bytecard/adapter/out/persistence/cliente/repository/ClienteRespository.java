package com.bytecard.adapter.out.persistence.cliente.repository;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRespository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByEmail(String email);

    @Query("""
    SELECT CASE
        WHEN COUNT(c) > 0 THEN true
        ELSE false
    END
    FROM ClienteEntity c
    WHERE c.email = :email OR c.cpf = :cpf
""")
    boolean existsByEmailOrCpf(@Param("email") String email, @Param("cpf") String cpf);

}
