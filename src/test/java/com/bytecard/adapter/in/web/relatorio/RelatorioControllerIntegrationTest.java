package com.bytecard.adapter.in.web.relatorio;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.relatorio.inputs.RelatorioGastosRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("RelatorioController - Integração")
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

    @Nested
    @DisplayName("Relatório de Gastos")
    class RelatorioDeGastosTests {

        @Test
        @DisplayName("Deve gerar relatório de gastos com sucesso")
        void deveGerarRelatorioDeGastosComSucesso() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("1234567812345678", YearMonth.now());

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
        @DisplayName("Deve retornar 404 se cartão não existir")
        void deveRetornar404SeCartaoNaoExistir() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("0000000000000000", YearMonth.now());

            mockMvc.perform(post("/relatorios")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(relatorioRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cartão não encontrado"));
        }

        @Test
        @DisplayName("Deve retornar 422 se não houver transações no período")
        void deveRetornar422SeNaoHouverTransacoesNoPeriodo() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("1234567812345678", YearMonth.of(2030, 1));

            mockMvc.perform(post("/relatorios")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(relatorioRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.message").value("Nenhuma compra realizada no período."));
        }

        @Test
        @DisplayName("Deve retornar 400 quando número do cartão estiver vazio")
        void deveRetornar400QuandoNumeroCartaoEstiverVazio() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("", YearMonth.now());

            mockMvc.perform(post("/relatorios")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(relatorioRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.numeroCartao").value("must not be blank"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando mesAno for nulo")
        void deveRetornar400QuandoMesAnoForNulo() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("1234567812345678", null);

            mockMvc.perform(post("/relatorios")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(relatorioRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mesAno").value("must not be null"));
        }

        @Test
        @DisplayName("Deve retornar 400 para JSON malformado")
        void deveRetornar400ParaJsonMalformado() throws Exception {
            String jsonInvalido = "{\"numeroCartao\": \"1234567812345678\", \"mesAno\": }";

            mockMvc.perform(post("/relatorios")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonInvalido))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 403 quando token não for enviado")
        void deveRetornar403SemToken() throws Exception {
            var relatorioRequest = new RelatorioGastosRequest("1234567812345678", YearMonth.now());

            mockMvc.perform(post("/relatorios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(relatorioRequest)))
                    .andExpect(status().isForbidden());
        }
    }
}
