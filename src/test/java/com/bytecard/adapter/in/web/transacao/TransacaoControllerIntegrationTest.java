package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.domain.model.CategoriaTransacao;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TransacaoController - Integração")
class TransacaoControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void obterToken() throws Exception {
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
    @DisplayName("POST /compras")
    class RegistrarCompra {

        @Test
        @DisplayName("Deve registrar compra com sucesso")
        void deveRegistrarCompraComSucesso() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("1234567812345678")
                    .estabelecimento("Restaurante Top")
                    .valor(BigDecimal.valueOf(150))
                    .categoria(CategoriaTransacao.ALIMENTACAO)
                    .build();

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.valor").value(150.00))
                    .andExpect(jsonPath("$.categoria").value("ALIMENTACAO"))
                    .andExpect(jsonPath("$.estabelecimento").value("Restaurante Top"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando estabelecimento for muito curto")
        void deveFalharQuandoEstabelecimentoForMuitoCurto() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("1234567812345678")
                    .estabelecimento("abc")
                    .valor(BigDecimal.valueOf(100))
                    .categoria(CategoriaTransacao.SAUDE)
                    .build();

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.estabelecimento").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando valor for negativo")
        void deveFalharQuandoValorForNegativo() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("1234567812345678")
                    .estabelecimento("Restaurante Top")
                    .valor(BigDecimal.valueOf(-100))
                    .categoria(CategoriaTransacao.OUTROS)
                    .build();

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.valor").exists());
        }

        @Test
        @DisplayName("Deve retornar 404 quando cartão não existir")
        void deveFalharQuandoCartaoNaoExistir() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("0000000000000000")
                    .estabelecimento("Restaurante Top")
                    .valor(BigDecimal.valueOf(100))
                    .categoria(CategoriaTransacao.CASA)
                    .build();

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cartão não encontrado"));
        }

        @Test
        @DisplayName("Deve retornar 422 quando limite for excedido")
        void deveFalharQuandoLimiteForExcedido() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("1234567812345678")
                    .estabelecimento("Compras Caras")
                    .valor(BigDecimal.valueOf(10000))
                    .categoria(CategoriaTransacao.OUTROS)
                    .build();

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.message").value("Limite insuficiente para realizar a compra."));
        }

        @Test
        @DisplayName("Deve retornar 403 se não houver token de autenticação")
        void deveFalharSemToken() throws Exception {
            var request = CriarCompraRequest.builder()
                    .cartaoNumero("1234567812345678")
                    .estabelecimento("Restaurante Top")
                    .valor(BigDecimal.valueOf(150))
                    .categoria(CategoriaTransacao.ALIMENTACAO)
                    .build();

            mockMvc.perform(post("/compras")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 400 quando JSON estiver vazio")
        void deveFalharComJsonVazio() throws Exception {
            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando JSON estiver malformado")
        void deveFalharComJsonMalformado() throws Exception {
            String jsonInvalido = "{\"cartaoNumero\": \"1234\""; // faltando fechamento

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonInvalido))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando categoria for inválida")
        void deveFalharComCategoriaInvalida() throws Exception {
            String json = """
                {
                  "cartaoNumero": "1234567812345678",
                  "estabelecimento": "Loja XYZ",
                  "valor": 50.0,
                  "categoria": "INVALIDA"
                }
            """;

            mockMvc.perform(post("/compras")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }
}
