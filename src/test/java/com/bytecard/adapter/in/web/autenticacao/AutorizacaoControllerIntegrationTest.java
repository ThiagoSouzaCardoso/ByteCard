package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("AutorizacaoController - Integração")
class AutorizacaoControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Login")
    class LoginTests {

        @Test
        @DisplayName("Deve autenticar com sucesso e retornar token")
        void deveAutenticarComSucessoERetornarToken() throws Exception {
            var login = new LoginRequest("gerente@bytecard.com", "admin");

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Deve retornar 401 para credenciais inválidas")
        void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
            var login = new LoginRequest("gerente@bytecard.com", "senhaErrada");

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 400 se usuário estiver vazio")
        void deveRetornar400SeUsuarioEstiverVazio() throws Exception {
            var login = new LoginRequest("", "admin");

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.username").value("O campo 'username' é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar 400 se senha estiver vazia")
        void deveRetornar400SeSenhaEstiverVazia() throws Exception {
            var login = new LoginRequest("gerente@bytecard.com", "");

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.password").value("O campo 'password' é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar 400 para login com JSON vazio")
        void deveRetornar400ParaLoginComJsonVazio() throws Exception {
            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando não enviar corpo na requisição")
        void deveRetornar400QuandoNaoEnviarCorpo() throws Exception {
            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 para JSON malformado")
        void deveRetornar400ParaJsonMalFormado() throws Exception {
            String jsonInvalido = "{\"username\": \"gerente@bytecard.com\", \"password\": }";

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonInvalido))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 se ambos os campos estiverem vazios")
        void deveRetornar400SeUsuarioESenhaEstiveremVazios() throws Exception {
            var login = new LoginRequest("", "");

            mockMvc.perform(post("/autorizacoes/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.username").value("O campo 'username' é obrigatório"))
                    .andExpect(jsonPath("$.password").value("O campo 'password' é obrigatório"));
        }
    }
}