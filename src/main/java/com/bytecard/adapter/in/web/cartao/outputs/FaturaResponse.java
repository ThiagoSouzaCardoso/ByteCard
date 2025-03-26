package com.bytecard.adapter.in.web.cartao.outputs;

import com.bytecard.domain.model.Fatura;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Schema(name = "FaturaResponse", description = "Representa a fatura mensal de um cartão")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaturaResponse extends RepresentationModel<FaturaResponse> {

   @Schema(description = "Número do cartão", example = "1234567812345678")
   private String cartaoNumero;

   @Schema(description = "Nome do cliente", example = "João da Silva")
   private String clienteNome;

   @Schema(description = "Mês da fatura", example = "2024-12")
   private YearMonth mes;

   @Schema(description = "Valor total da fatura", example = "1399.90")
   private BigDecimal valorTotal;

   @Schema(description = "Lista de compras realizadas")
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


