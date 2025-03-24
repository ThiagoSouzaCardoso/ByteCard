package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.transacao.inputs.RelatorioGastosRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RelatorioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        var login = LoginRequest.builder()
                .username("gerente@bytecard.com")
                .password("admin")
                .build();

        MvcResult result = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class).token();
    }

    @Test
    void deveGerarRelatorioDeGastosComSucesso() throws Exception {

       var relatorioRequest = new RelatorioGastosRequest("1234567812345678",YearMonth.now());

        mockMvc.perform(post("/relatorios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relatorioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartaoNumero").value("1234567812345678"))
                .andExpect(jsonPath("$.gastos").isArray())
                .andExpect(jsonPath("$.valorTotalGasto").isNumber());
    }

    @Test
    void deveRetornar404SeCartaoNaoExistir() throws Exception {

        var relatorioRequest = new RelatorioGastosRequest("0000000000000000",YearMonth.now());

        mockMvc.perform(post("/relatorios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relatorioRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cartão não encontrado"));
    }

    @Test
    void deveRetornar422SeNaoHouverTransacoesNoPeriodo() throws Exception {

        var relatorioRequest = new RelatorioGastosRequest("1234567812345678",YearMonth.of(2030, 1));

        mockMvc.perform(post("/relatorios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relatorioRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Nenhuma compra realizada no período."));
    }

    @Test
    void deveRetornar400QuandoNumeroCartaoEstiverVazio() throws Exception {

        var relatorioRequest = new RelatorioGastosRequest("",YearMonth.now());

        mockMvc.perform(post("/relatorios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relatorioRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numeroCartao").value("must not be blank"));
    }

    @Test
    void deveRetornar400QuandoMesAnoForNulo() throws Exception {

        var relatorioRequest = new RelatorioGastosRequest("1234567812345678",null);

        mockMvc.perform(post("/relatorios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(relatorioRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mesAno").value("must not be null"));
    }
}
