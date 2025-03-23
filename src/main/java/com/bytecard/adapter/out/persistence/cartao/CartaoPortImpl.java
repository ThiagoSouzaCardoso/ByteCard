package com.bytecard.adapter.out.persistence.cartao;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class CartaoPortImpl implements BuscaCartaoPort, RegistraCartaoPort {

    private CartaoRepository cartaoRepository;
    private ClienteRespository clienteRespository;


    @Override
    public Cartao save(Cartao cartao) {

       var cliente = clienteRespository.findByEmail(cartao.getCliente().email())
               .orElseThrow(() -> new ClienteNotFoundException("Usuário não encontrado"));

        var cartaoEntity = CartaoEntity.builder()
                .id(cartao.getId())
                .cvv(cartao.getCvv())
                .numero(cartao.getNumero())
                .validade(cartao.getValidade())
                .status(cartao.getStatus())
                .limite(cartao.getLimite())
                .limiteUtilizado(cartao.getLimiteUtilizado())
                .cliente(cliente)
                .build();


       CartaoEntity cartaoRegistrado = cartaoRepository.save(cartaoEntity);

        return converterParaCartao(cartaoRegistrado);
    }

    @Override
    public Page<Cartao> findAllOrdenadosPaginados(Integer numeroPagina, Integer tamanhoPagina,String cpf, String numeroCartao) {

        Pageable pageable = PageRequest.of(numeroPagina, tamanhoPagina);
        Page<CartaoEntity> result = cartaoRepository.findAllOrdered(cpf,numeroCartao,pageable);

        List<Cartao> cartoes = result.getContent().stream()
                .map(this::converterParaCartao)
                .collect(Collectors.toList());

        return new PageImpl<>(cartoes, pageable, result.getTotalElements());
    }

    @Override
    public Optional<Cartao> findById(Long id) {
        Optional<CartaoEntity> cartao = cartaoRepository.findById(id);
        return cartao.map(this::converterParaCartao);
    }

    @Override
    public Optional<Cartao> findByNumero(String numero) {
        var cartao = cartaoRepository.findByNumero(numero);
        return cartao.map(this::converterParaCartao);
    }


    private Cartao converterParaCartao(CartaoEntity cartaoEntity) {
        return Cartao.builder()
                .id(cartaoEntity.getId())
                .numero(cartaoEntity.getNumero())
                .limite(cartaoEntity.getLimite())
                .limiteUtilizado(cartaoEntity.getLimiteUtilizado())
                .cvv(cartaoEntity.getCvv())
                .validade(cartaoEntity.getValidade())
                .status(cartaoEntity.getStatus())
                .cliente(Cliente.builder()
                        .nome(cartaoEntity.getCliente().getNome())
                        .email(cartaoEntity.getCliente().getEmail())
                        .build())
                .build();
    }

}