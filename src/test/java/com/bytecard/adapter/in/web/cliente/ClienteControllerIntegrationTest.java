package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
@DisplayName("ClienteController - Integração")
class ClienteControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

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

    @Nested
    @DisplayName("Cadastro de Cliente")
    class CadastroClienteTests {

        @Test
        @DisplayName("Deve cadastrar novo cliente com sucesso com perfil GERENTE")
        void deveCadastrarNovoClienteComSucesso() throws Exception {
            var token = obterTokenValido("gerente@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("João")
                    .cpf("12345678900")
                    .email("joao@email.com")
                    .senha("senha123")
                    .papel("CLIENTE")
                    .build();

            mockMvc.perform(post("/clientes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Não deve permitir cadastro de cliente com perfil USUARIO")
        void naoDeveCadastrarNovoClienteComUsuarioPerfilUsuario() throws Exception {
            var token = obterTokenValido("usuario@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("João")
                    .cpf("12345678900")
                    .email("joao@email.com")
                    .senha("senha123")
                    .papel("CLIENTE")
                    .build();

            mockMvc.perform(post("/clientes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Validações de Dados")
    class ValidacoesTests {

        @Test
        @DisplayName("Não deve cadastrar cliente com e-mail inválido")
        void naoDeveCadastrarClienteComEmailInvalido() throws Exception {
            String token = obterTokenValido("gerente@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("Maria")
                    .cpf("12345678900")
                    .email("email-invalido")
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
        @DisplayName("Não deve cadastrar cliente com CPF inválido")
        void naoDeveCadastrarClienteComCpfInvalido() throws Exception {
            String token = obterTokenValido("gerente@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("Maria")
                    .cpf("123")
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
        @DisplayName("Não deve cadastrar cliente com senha curta")
        void naoDeveCadastrarClienteComSenhaCurta() throws Exception {
            String token = obterTokenValido("gerente@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("Maria")
                    .cpf("12345678900")
                    .email("maria@email.com")
                    .senha("123")
                    .papel("CLIENTE")
                    .build();

            mockMvc.perform(post("/clientes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Não deve cadastrar cliente com papel inválido")
        void naoDeveCadastrarClienteComPapelInvalido() throws Exception {
            String token = obterTokenValido("gerente@bytecard.com", "admin");

            var request = NovoClienteRequest.builder()
                    .nome("Maria")
                    .cpf("12345678900")
                    .email("maria@email.com")
                    .senha("senha123")
                    .papel("USUARIO")
                    .build();

            mockMvc.perform(post("/clientes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
