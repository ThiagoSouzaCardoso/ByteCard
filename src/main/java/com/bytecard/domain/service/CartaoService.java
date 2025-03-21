package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Cliente;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartaoService implements CartaoUseCase {

    private final CartaoRepository cartaoRepository;
    private final BuscaClientePort buscaCartaoPort;
    private final RegistraCartaoPort registraCartaoPort;

    private static final SecureRandom random = new SecureRandom();


    public CartaoService(CartaoRepository cartaoRepository, BuscaClientePort buscaCartaoPort, RegistraCartaoPort registraCartaoPort) {
        this.cartaoRepository = cartaoRepository;
        this.buscaCartaoPort = buscaCartaoPort;
        this.registraCartaoPort = registraCartaoPort;
    }


    @Override
    public Cartao register(Cartao cartao) {

        var cliente = buscaCartaoPort.findClienteByEmail(cartao.getCliente().email());
        if(cliente.isEmpty()){
            throw new ClienteNotFoundException("Usuário não encontrado");
        }

        var cartaoSalvar = Cartao.builder()
                .cvv(gerarCVV())
                .numero(gerarNumeroCartao())
                .validade(YearMonth.now().plusYears(4).plusMonths(6))
                .status(StatusCartao.ATIVO)
                .limite(cartao.getLimite())
                .cliente(cliente.get())
                .build();

        return registraCartaoPort.save(cartaoSalvar);
    }

    @Override
    public Page<Cartao> getAllCartoes(Integer pageNo, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<CartaoEntity> result = cartaoRepository.findAllOrdered(pageable);

        List<Cartao> cartoes = result.getContent().stream()
                .map(this::converterParaCartao)
                .collect(Collectors.toList());

        return new PageImpl<>(cartoes, pageable, result.getTotalElements());
    }

    @Override
    public Cartao alterarLimit(BigDecimal limite, Long id) {
        Optional<CartaoEntity> cartao = cartaoRepository.findById(id);

        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }

        CartaoEntity cartaoEncontrado = cartao.get();
        cartaoEncontrado.setLimite(limite);
        CartaoEntity cartaoAtualizado = cartaoRepository.save(cartaoEncontrado);

        return converterParaCartao(cartaoAtualizado);
    }

    @Override
    public Cartao alterarStatusCartao(Long id, String status) {
        Optional<CartaoEntity> cartao = cartaoRepository.findById(id);

        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }

        CartaoEntity cartaoEncontrado = cartao.get();
        cartaoEncontrado.setStatus(StatusCartao.valueOf(status));

        CartaoEntity cartaoAtualizado = cartaoRepository.save(cartaoEncontrado);

        return converterParaCartao(cartaoAtualizado);
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



    private Cartao converterParaCartao(CartaoEntity cartaoEntity) {
        return Cartao.builder()
                .id(cartaoEntity.getId())
                .numero(cartaoEntity.getNumero())
                .limite(cartaoEntity.getLimite())
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