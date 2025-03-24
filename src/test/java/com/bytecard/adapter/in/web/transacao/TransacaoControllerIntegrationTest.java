package com.bytecard.adapter.in.web.transacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.transacao.inputs.CriarCompraRequest;
import com.bytecard.domain.model.CategoriaTransacao;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @Test
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
}

