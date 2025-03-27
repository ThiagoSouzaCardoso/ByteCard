package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CartaoController - Integração")
class CartaoControllerIntegrationTest {

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
    @DisplayName("Cadastro e Atualização de Cartão")
    class CadastroCartao {

        @Test
        @DisplayName("Deve cadastrar cartão com sucesso")
        void deveCadastrarCartaoComSucesso() throws Exception {
            var request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "usuario@bytecard.com");

            mockMvc.perform(post("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Não deve cadastrar cartão com limite inválido")
        void naoDeveCadastrarCartaoComLimiteInvalido() throws Exception {
            var request = new CriarCartaoRequest(BigDecimal.ZERO, "cliente@bytecard.com");

            mockMvc.perform(post("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Não deve cadastrar cartão sem cliente")
        void naoDeveCadastrarCartaoSemCliente() throws Exception {
            var request = new CriarCartaoRequest(BigDecimal.valueOf(500), "");

            mockMvc.perform(post("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Não deve permitir cadastro com usuário sem permissão")
        void naoDevePermitirCadastroComUsuarioSemPermissao() throws Exception {
            String tokenCliente = obterTokenValido("usuario@bytecard.com", "admin");
            var request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "cliente@bytecard.com");

            mockMvc.perform(post("/cartoes")
                            .header("Authorization", "Bearer " + tokenCliente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Alteração de Limite")
    class AlterarLimite {

        @Test
        @DisplayName("Deve alterar limite com sucesso")
        void deveAlterarLimiteComSucesso() throws Exception {
            var novoLimite = new AlterarLimitRequest(BigDecimal.valueOf(2000.00));

            mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoLimite)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve retornar 400 quando novo limite for nulo")
        void deveRetornar400QuandoNovoLimiteForNulo() throws Exception {
            var novoLimite = new AlterarLimitRequest(null);

            mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoLimite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.novoLimite").value("O novo limite é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando novo limite for zero")
        void deveRetornar400QuandoNovoLimiteForZero() throws Exception {
            var novoLimite = new AlterarLimitRequest(BigDecimal.ZERO);

            mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoLimite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.novoLimite").value("O limite deve ser maior que zero"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando novo limite for negativo")
        void deveRetornar400QuandoNovoLimiteForNegativo() throws Exception {
            var novoLimite = new AlterarLimitRequest(BigDecimal.valueOf(-2000.00));

            mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoLimite)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.novoLimite").value("O limite deve ser maior que zero"));
        }
    }

    @Nested
    @DisplayName("Visualização e Listagem")
    class Visualizacao {

        @Test
        @DisplayName("Deve listar cartões com sucesso")
        void deveListarCartoesComSucesso() throws Exception {
            mockMvc.perform(get("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .param("pageNo", "0")
                            .param("pageSize", "5"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve listar cartões filtrando por CPF e número")
        void deveListarCartoesFiltrandoPorCpfENumero() throws Exception {
            mockMvc.perform(get("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .param("cpf", "12345678900")
                            .param("numero", "1234567812345678"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve visualizar fatura com sucesso")
        void deveVisualizarFatura() throws Exception {
            mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                            .header("Authorization", "Bearer " + token)
                            .param("mesAno", YearMonth.now().toString()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Não deve visualizar fatura sem transações")
        void naoDeveVisualizarFaturaSemTransacoes() throws Exception {
            mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                            .header("Authorization", "Bearer " + token)
                            .param("mesAno", "2024-12"))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("Deve retornar 400 para formato inválido de mesAno")
        void deveRetornar400QuandoFormatoDeMesAnoForInvalido() throws Exception {
            mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                            .header("Authorization", "Bearer " + token)
                            .param("mesAno", "12-2024"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").value("Formato inválido para o parâmetro 'mesAno'. Esperado: yyyy-MM"));
        }
    }

    @Nested
    @DisplayName("Status do Cartão")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class StatusCartao {

        @Test
        @Order(1)
        @DisplayName("Deve bloquear cartão com sucesso")
        void deveBloquearCartaoComSucesso() throws Exception {
            mockMvc.perform(patch("/cartoes/1234567812345678/bloquear")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @Order(2)
        @DisplayName("Deve ativar cartão com sucesso")
        void deveAtivarCartaoComSucesso() throws Exception {
            mockMvc.perform(patch("/cartoes/1234567812345678/ativar")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @Order(3)
        @DisplayName("Deve cancelar cartão com sucesso")
        void deveCancelarCartaoComSucesso() throws Exception {
            mockMvc.perform(patch("/cartoes/1234567812345678/cancelar")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @Order(4)
        @DisplayName("Deve dar erro ao tentar ativar um  cartão concelado")
        void deveDarErroAoTentarAtivarUmCartãoConcelado() throws Exception {
            mockMvc.perform(patch("/cartoes/1234567812345678/cancelar")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnprocessableEntity());
        }

    }

    @Nested
    @DisplayName("Autenticação e Segurança")
    class AuthTests {

        @Test
        @DisplayName("Deve retornar 400 para parâmetros de paginação inválidos")
        void deveRetornar400ParaParametrosDePaginacaoInvalidos() throws Exception {
            mockMvc.perform(get("/cartoes")
                            .header("Authorization", "Bearer " + token)
                            .param("pagina", "-1")
                            .param("tamanhoPagina", "0"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Não deve permitir acesso sem token")
        void naoDevePermitirAcessoSemToken() throws Exception {
            mockMvc.perform(get("/cartoes"))
                    .andExpect(status().isForbidden());
        }
    }

    private String obterTokenValido(String username, String password) throws Exception {
        var loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/autorizacoes/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class).token();
    }
}