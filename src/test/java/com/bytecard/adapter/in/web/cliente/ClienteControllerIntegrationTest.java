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

        var token = obterTokenValido("gerente@bytecard.com", "admin");

       var request = NovoClienteRequest.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE").build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void naoDeveCadastrarNovoClienteComUsuarioPerfilUsuario() throws Exception {


       var token = obterTokenValido("usuario@bytecard.com", "admin");

        var request = NovoClienteRequest.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senha("senha123")
                .papel("CLIENTE").build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    private String obterTokenValido(String username, String password) throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult loginResult = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class).token();
    }


    @Test
    void naoDeveCadastrarClienteComEmailInvalido() throws Exception {
        String token = obterTokenValido("gerente@bytecard.com", "admin");

        var request = NovoClienteRequest.builder()
                .nome("Maria")
                .cpf("12345678900")
                .email("email-invalido") // inválido
                .senha("senha123")
                .papel("CLIENTE")
                .build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarClienteComCpfInvalido() throws Exception {
        String token = obterTokenValido("gerente@bytecard.com", "admin");

        var request = NovoClienteRequest.builder()
                .nome("Maria")
                .cpf("123") // inválido
                .email("maria@email.com")
                .senha("senha123")
                .papel("CLIENTE")
                .build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarClienteComSenhaCurta() throws Exception {
        String token = obterTokenValido("gerente@bytecard.com", "admin");

        var request = NovoClienteRequest.builder()
                .nome("Maria")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("123") // muito curta
                .papel("CLIENTE")
                .build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarClienteComPapelInvalido() throws Exception {
        String token = obterTokenValido("gerente@bytecard.com", "admin");

        var request = NovoClienteRequest.builder()
                .nome("Maria")
                .cpf("12345678900")
                .email("maria@email.com")
                .senha("senha123")
                .papel("USUARIO") // inválido
                .build();

        mockMvc.perform(post("/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }



}

