package com.bytecard.domain.port.in.cartao;

import com.bytecard.domain.model.Cartao;
import com.bytecard.domain.model.Fatura;
import com.bytecard.domain.model.StatusCartao;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface CartaoUseCase {

    Cartao register(Cartao cartao);

    Page<Cartao> getAllCartoes(Integer pageNo, Integer pageSize,String cpf, String numeroCartao);

    Cartao alterarLimit(BigDecimal limite, String numeroCartao);

    Cartao alterarStatusCartao(String numero, StatusCartao status);

    Fatura gerarFaturaPorNumero(String numeroCartao, YearMonth mesAno);
}