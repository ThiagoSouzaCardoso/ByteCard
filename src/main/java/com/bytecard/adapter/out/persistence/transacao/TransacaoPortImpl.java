package com.bytecard.adapter.out.persistence.transacao;

import com.bytecard.adapter.out.persistence.cartao.entity.CartaoEntity;
import com.bytecard.adapter.out.persistence.cartao.repository.CartaoRepository;
import com.bytecard.adapter.out.persistence.transacao.entity.GastoCategoriaView;
import com.bytecard.adapter.out.persistence.transacao.entity.TransacaoEntity;
import com.bytecard.adapter.out.persistence.transacao.repository.GastoCategoriaViewRepository;
import com.bytecard.adapter.out.persistence.transacao.repository.TransacaoRepository;
import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.GastoCategoria;
import com.bytecard.domain.model.Transacao;
import com.bytecard.domain.port.out.transacao.BuscaTransacaoPort;
import com.bytecard.domain.port.out.transacao.RegistrarTransacaoPort;
import com.bytecard.domain.port.out.transacao.RelatorioTransacaoPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class TransacaoPortImpl implements RegistrarTransacaoPort, RelatorioTransacaoPort , BuscaTransacaoPort {

    private TransacaoRepository transacaoRepository;
    private CartaoRepository cartaoRepository;
    private GastoCategoriaViewRepository gastoCategoriaViewRepository;

    @Override
    public Transacao registrar(Transacao novaTransacao) {

       CartaoEntity cartao = cartaoRepository.findByNumero(novaTransacao.getCartao().getNumero()).get();

        TransacaoEntity transacaoEntity = TransacaoEntity.builder()
                .cartao(cartao)
                .valor(novaTransacao.getValor())
                .categoria(novaTransacao.getCategoria())
                .estabelecimento(novaTransacao.getEstabelecimento())
                .build();

        TransacaoEntity transacaoSalva = transacaoRepository.save(transacaoEntity);


        CartaoEntity cartaoUtilizado = transacaoSalva.getCartao();

        return  Transacao.builder()
                .id(transacaoSalva.getId())
                .valor(transacaoSalva.getValor())
                .estabelecimento(transacaoSalva.getEstabelecimento())
                .categoria(transacaoSalva.getCategoria())
                .data(transacaoSalva.getDataHora())
                .cartao(Cartao.builder()
                        .numero(cartaoUtilizado.getNumero())
                        .limite(cartaoUtilizado.getLimite())
                        .status(cartaoUtilizado.getStatus())
                        .validade(cartaoUtilizado.getValidade())
                        .build())
                .build();
    }

    @Override
    public List<GastoCategoria> getSomatorioGastosPorCategoriaNoMes(Long id, Integer ano, Integer mes) {
        List<GastoCategoriaView> resultadoView = gastoCategoriaViewRepository.findByIdCartaoIdAndIdAnoAndIdMesOrderByIdCategoria(id, ano, mes);

        return resultadoView.stream()
                .map(view -> new GastoCategoria(
                        view.getId().getCategoria(),
                        view.getTotal()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<Transacao> findByCartaoNumeroAndMes(String numeroCartao, Integer year, Integer monthValue) {

       var transacoes = transacaoRepository.findByCartaoNumeroAndMes(numeroCartao,year,monthValue);
       return transacoes.stream().map(trasacaoEntity ->
               Transacao.builder()
                       .id(trasacaoEntity.getId())
                       .valor(trasacaoEntity.getValor())
                       .estabelecimento(trasacaoEntity.getEstabelecimento())
                       .categoria(trasacaoEntity.getCategoria())
                       .data(trasacaoEntity.getDataHora())
                       .build())
               .toList();
    }
}