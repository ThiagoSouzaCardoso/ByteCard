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
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Component;

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
                .cvv(cartao.getCvv())
                .numero(cartao.getNumero())
                .validade(cartao.getValidade())
                .status(cartao.getStatus())
                .limite(cartao.getLimite())
                .cliente(cliente)
                .build();


       CartaoEntity cartaoRegistrado = cartaoRepository.save(cartaoEntity);

        return converterParaCartao(cartaoRegistrado);
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