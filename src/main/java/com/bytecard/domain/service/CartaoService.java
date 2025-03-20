package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.entity.StatusCartao;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import com.bytecard.domain.port.out.cartao.CartaoPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.List;

@Service
public class CartaoService implements CartaoUseCase {

    private final CartaoRepository cartaoRepository;
    private final ClienteRespository clienteRespository;
    private final CartaoPort cartaoPort;

    private static final SecureRandom random = new SecureRandom();


    public CartaoService(CartaoRepository cartaoRepository, ClienteRespository clienteRespository, CartaoPort cartaoPort) {
        this.cartaoRepository = cartaoRepository;
        this.clienteRespository = clienteRespository;
        this.cartaoPort = cartaoPort;
    }


    @Override
    public Cartao register(Cartao cartao) {

        var cliente = clienteRespository.findByEmail(cartao.getCliente().email());
        if(cliente.isEmpty()){
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

         var cartaoEntity = CartaoEntity.builder()
                 .cvv(gerarCVV())
                 .numero(gerarNumeroCartao())
                 .validade(YearMonth.now().plusYears(4).plusMonths(6))
                 .status(StatusCartao.ATIVO)
                 .limite(cartao.getLimite())
                 .cliente(cliente.get())
                 .build();

        var cartaoGerado = cartaoRepository.save(cartaoEntity);
        return Cartao.builder()
                .id(cartaoGerado.getId())
                .numero(cartaoGerado.getNumero())
                .limite(cartaoGerado.getLimite())
                .cvv(cartaoGerado.getCvv())
                .validade(cartaoGerado.getValidade())
                .status(cartaoGerado.getStatus().name())
                .cliente(Cliente.builder()
                        .nome(cartaoGerado.getCliente().getNome())
                        .email(cartaoGerado.getCliente().getEmail())
                        .build())
                .build();
    }

    @Override
    public List<Cartao> getAllCartoes() {
        return cartaoRepository.findAllOrdered().stream().map(cartaoEntity -> {
            return Cartao.builder()
                   .id(cartaoEntity.getId())
                   .numero(cartaoEntity.getNumero())
                   .limite(cartaoEntity.getLimite())
                   .cvv(cartaoEntity.getCvv())
                   .validade(cartaoEntity.getValidade())
                   .status(cartaoEntity.getStatus().name())
                   .cliente(Cliente.builder()
                           .nome(cartaoEntity.getCliente().getNome())
                           .email(cartaoEntity.getCliente().getEmail())
                           .build())
                   .build();
                }
        ).toList();

    }

    private String gerarNumeroCartao() {
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            numero.append(random.nextInt(10));
        }
        return numero.toString();
    }

    private String gerarCVV() {
        return String.format("%03d", random.nextInt(1000));
    }


}