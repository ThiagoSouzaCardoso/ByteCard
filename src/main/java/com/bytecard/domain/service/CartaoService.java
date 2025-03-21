package com.bytecard.domain.service;

import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.YearMonth;

@Service
public class CartaoService implements CartaoUseCase {

    private final BuscaClientePort buscaClientePort;
    private final BuscaCartaoPort buscaCartaoPort;
    private final RegistraCartaoPort registraCartaoPort;

    private static final SecureRandom random = new SecureRandom();


    public CartaoService(BuscaClientePort buscaClientePort, BuscaCartaoPort buscaCartaoPort, RegistraCartaoPort registraCartaoPort) {
        this.buscaClientePort = buscaClientePort;
        this.buscaCartaoPort = buscaCartaoPort;
        this.registraCartaoPort = registraCartaoPort;
    }


    @Override
    public Cartao register(Cartao cartao) {

        var cliente = buscaClientePort.findClienteByEmail(cartao.getCliente().email());
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
        return buscaCartaoPort.findAllOrdenadosPaginados(pageNo,pageSize);
    }

    @Override
    public Cartao alterarLimit(BigDecimal limite, Long id) {

        var cartao = buscaCartaoPort.findById(id);
        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }

        Cartao cartaoEncontrado = cartao.get();
        cartaoEncontrado.setLimite(limite);

        return registraCartaoPort.save(cartaoEncontrado);
    }

    @Override
    public Cartao alterarStatusCartao(Long id, StatusCartao status) {
        var cartao = buscaCartaoPort.findById(id);
        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }
        Cartao cartaoEncontrado = cartao.get();
        cartaoEncontrado.setStatus(status);
        return registraCartaoPort.save(cartaoEncontrado);
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