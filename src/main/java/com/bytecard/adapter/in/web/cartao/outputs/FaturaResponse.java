package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.domain.model.Fatura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FaturaResponse extends RepresentationModel<FaturaResponse> {

   private String cartaoNumero;
   private String clienteNome;
   private YearMonth mes;
   private BigDecimal valorTotal;
   private List<CompraItemResponse> compras;


   public static FaturaResponse from(Fatura fatura) {

      return FaturaResponse.builder()
              .cartaoNumero(fatura.cartao().getNumero())
              .clienteNome(fatura.cliente().nome())
              .mes(fatura.mes())
              .valorTotal(fatura.valorTotal())
              .compras(fatura.compras()
                      .stream()
                      .map(CompraItemResponse::from)
                      .collect(Collectors.toList()))
              .build();
   }

}


