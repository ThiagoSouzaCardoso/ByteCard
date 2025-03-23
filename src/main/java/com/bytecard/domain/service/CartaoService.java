package com.bytecard.domain.service;

import com.bytecard.adapter.in.web.cartao.outputs.CompraItemResponse;
import com.bytecard.adapter.in.web.cartao.outputs.FaturaResponse;
import com.bytecard.domain.exception.CartaoNotFoundException;
import com.bytecard.domain.exception.ClienteNotFoundException;
import com.bytecard.domain.exception.RelatorioEmptyException;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.StatusCartao;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.in.cartao.CartaoUseCase;
import com.bytecard.domain.port.out.cartao.BuscaCartaoPort;
import com.bytecard.domain.port.out.cartao.RegistraCartaoPort;
import com.bytecard.domain.port.out.cliente.BuscaClientePort;
import com.bytecard.domain.port.out.transacao.BuscaTransacaoPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class CartaoService implements CartaoUseCase {

    private final BuscaClientePort buscaClientePort;
    private final BuscaCartaoPort buscaCartaoPort;
    private final RegistraCartaoPort registraCartaoPort;
    private final BuscaTransacaoPort buscaTransacaoPort;

    private static final SecureRandom random = new SecureRandom();


    public CartaoService(BuscaClientePort buscaClientePort, BuscaCartaoPort buscaCartaoPort, RegistraCartaoPort registraCartaoPort, BuscaTransacaoPort buscaTransacaoPort) {
        this.buscaClientePort = buscaClientePort;
        this.buscaCartaoPort = buscaCartaoPort;
        this.registraCartaoPort = registraCartaoPort;
        this.buscaTransacaoPort = buscaTransacaoPort;
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
    public Page<Cartao> getAllCartoes(Integer pageNo, Integer pageSize,String cpf, String numeroCartao) {
        return buscaCartaoPort.findAllOrdenadosPaginados(pageNo,pageSize,cpf,numeroCartao);
    }

    @Override
    public Cartao alterarLimit(BigDecimal limite, String numeroCartao) {

        var cartao = buscaCartaoPort.findByNumero(numeroCartao);
        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }

        Cartao cartaoEncontrado = cartao.get();
        cartaoEncontrado.setLimite(limite);

        return registraCartaoPort.save(cartaoEncontrado);
    }

    @Override
    public Cartao alterarStatusCartao(String numeroCartao, StatusCartao status) {
        var cartao = buscaCartaoPort.findByNumero(numeroCartao);
        if(cartao.isEmpty()){
            throw new CartaoNotFoundException("Cartão não Encontrado");
        }
        Cartao cartaoEncontrado = cartao.get();
        cartaoEncontrado.setStatus(status);
        return registraCartaoPort.save(cartaoEncontrado);
    }

    @Override
    public FaturaResponse gerarFaturaPorNumero(String numeroCartao, YearMonth mesAno) {

     Cartao  cartao = buscaCartaoPort.findByNumero(numeroCartao).orElseThrow(
             () -> new CartaoNotFoundException("Cartão não encontrado")
     );

     List<Transacao> compras = buscaTransacaoPort.findByCartaoNumeroAndMes(
                numeroCartao, mesAno.getYear(), mesAno.getMonthValue()
        );

        if (compras.isEmpty()) {
            throw new RelatorioEmptyException("Nenhuma compra realizada no período.");
        }

        BigDecimal valorTotal = compras.stream()
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CompraItemResponse> itens = compras.stream()
                .sorted(Comparator.comparing(Transacao::getData))
                .map(CompraItemResponse::from)
                .toList();

        return new FaturaResponse(
                cartao.getNumero(),
                cartao.getCliente().nome(),
                mesAno,
                valorTotal,
                itens
        );

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