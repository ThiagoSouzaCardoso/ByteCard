package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutorizacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveAutenticarComSucessoERetornarToken() throws Exception {
        var login = new LoginRequest("gerente@bytecard.com", "admin");

        mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        var login = new LoginRequest("gerente@bytecard.com", "senhaErrada");

        mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar400SeUsuarioEstiverVazio() throws Exception {
        var login = new LoginRequest("", "admin");

        mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("O campo 'username' é obrigatório"));
    }

    @Test
    void deveRetornar400SeSenhaEstiverVazia() throws Exception {
        var login = new LoginRequest("gerente@bytecard.com", "");

        mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("O campo 'password' é obrigatório"));
    }

    @Test
    void deveRetornar400ParaLoginComJsonVazio() throws Exception {
        mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

