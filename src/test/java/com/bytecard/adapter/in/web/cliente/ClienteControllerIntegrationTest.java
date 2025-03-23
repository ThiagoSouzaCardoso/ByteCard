package com.bytecard.adapter.in.web.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarNovoClienteComSucesso() throws Exception {

        String loginRequest = "username=gerente@bytecard.com&password=admin";

        MvcResult loginResult = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String token = loginResult.getResponse().getContentAsString();

        // Agora faz a requisição com o token
        String requestJson = """
        {
            "nome": "João",
            "cpf": "12345678900",
            "email": "joao@email.com",
            "senha": "senha123",
            "papel": "CLIENTE"
        }
        """;

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveCadastrarNovoClienteComUsuarioPerfilUsuario() throws Exception {

        String loginRequest = "username=usuario@bytecard.com&password=admin";

        MvcResult loginResult = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String token = loginResult.getResponse().getContentAsString();

        String requestJson = """
        {
            "nome": "João",
            "cpf": "12345678901",
            "email": "joao2@email.com",
            "senha": "senha123",
            "papel": "CLIENTE"
        }
        """;

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }



}

