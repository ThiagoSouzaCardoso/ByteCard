package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarNovoClienteComSucesso() throws Exception {

        LoginRequest loginRequest =
                LoginRequest.builder()
                        .username("gerente@bytecard.com")
                        .password("admin")
                        .build();

        MvcResult loginResult = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TokenResponse token = objectMapper.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);;

       var request = NovoClienteRequest.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE").build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveCadastrarNovoClienteComUsuarioPerfilUsuario() throws Exception {

        LoginRequest loginRequest =
                LoginRequest.builder()
                        .username("usuario@bytecard.com")
                        .password("admin")
                        .build();

        MvcResult loginResult = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TokenResponse token = objectMapper.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);;

        var request = NovoClienteRequest.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE").build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}

