package com.bytecard.domain.port.out.cartao;

import com.bytecard.domain.model.Cartao;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface BuscaCartaoPort {

    Page<Cartao> findAllOrdenadosPaginados(Integer numeroPagina, Integer tamanhoPagina);

    Optional<Cartao> findById(Long id);
}