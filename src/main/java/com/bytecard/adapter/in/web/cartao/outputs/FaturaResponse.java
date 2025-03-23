package com.bytecard.adapter.in.web.cartao.outputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaturaResponse extends RepresentationModel<FaturaResponse> {

   private String cartaoNumero;
   private String clienteNome;
   private YearMonth mes;
   private BigDecimal valorTotal;
   private List<CompraItemResponse> compras;

}


