package com.bytecard.adapter.in.web.cartao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.adapter.in.web.autenticacao.output.TokenResponse;
import com.bytecard.adapter.in.web.cartao.inputs.AlterarLimitRequest;
import com.bytecard.adapter.in.web.cartao.inputs.CriarCartaoRequest;
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
import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @Test
    void deveCadastrarCartaoComSucesso() throws Exception {
        var request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "usuario@bytecard.com");

        mockMvc.perform(post("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveListarCartoesComSucesso() throws Exception {
        mockMvc.perform(get("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .param("pageNo", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void deveAlterarLimiteComSucesso() throws Exception {

      var novoLimite =  new AlterarLimitRequest(BigDecimal.valueOf(2000.00));

        mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoLimite)))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveVisualizarFaturaSemTransacoes() throws Exception {
        mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                        .header("Authorization", "Bearer " + token)
                        .param("mesAno", "2024-12"))
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    void deveVisualizarFatura() throws Exception {
        mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                        .header("Authorization", "Bearer " + token)
                        .param("mesAno", YearMonth.now().toString()))
                .andExpect(status().isOk());
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

    @Test
    void naoDeveCadastrarCartaoComLimiteInvalido() throws Exception {
        var request = new CriarCartaoRequest(BigDecimal.ZERO, "cliente@bytecard.com");

        mockMvc.perform(post("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void naoDeveCadastrarCartaoSemCliente() throws Exception {
        var request = new CriarCartaoRequest(BigDecimal.valueOf(500), "");

        mockMvc.perform(post("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void naoDevePermitirCadastroComUsuarioSemPermissao() throws Exception {
        String tokenCliente = obterTokenValido("usuario@bytecard.com", "admin");

        var request = new CriarCartaoRequest(BigDecimal.valueOf(1000), "cliente@bytecard.com");

        mockMvc.perform(post("/cartoes")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    @Test
    void deveListarCartoesFiltrandoPorCpfENumero() throws Exception {
        mockMvc.perform(get("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .param("cpf", "12345678900")
                        .param("numero", "1234567812345678"))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar400QuandoNovoLimiteForNulo() throws Exception {
        var novoLimite =  new AlterarLimitRequest(null);

        mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoLimite)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.novoLimite").value("O novo limite é obrigatório"));
    }

    @Test
    void deveRetornar400QuandoNovoLimiteForZero() throws Exception {
        var novoLimite =  new AlterarLimitRequest(BigDecimal.ZERO);
        mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoLimite)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.novoLimite").value("O limite deve ser maior que zero"));
    }

    @Test
    void deveRetornar400QuandoNovoLimiteForNegativo() throws Exception {

        var novoLimite =  new AlterarLimitRequest(BigDecimal.valueOf(-2000.00));


        mockMvc.perform(patch("/cartoes/1234567812345678/alterar-limite")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoLimite)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.novoLimite").value("O limite deve ser maior que zero"));
    }

    @Test
    void deveAtivarCartaoComSucesso() throws Exception {
        mockMvc.perform(patch("/cartoes/1234567812345678/ativar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveCancelarCartaoComSucesso() throws Exception {
        mockMvc.perform(patch("/cartoes/1234567812345678/cancelar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearCartaoComSucesso() throws Exception {
        mockMvc.perform(patch("/cartoes/1234567812345678/bloquear")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }


    @Test
    void deveRetornar400ParaParametrosDePaginacaoInvalidos() throws Exception {
        mockMvc.perform(get("/cartoes")
                        .header("Authorization", "Bearer " + token)
                        .param("pageNo", "-1")
                        .param("pageSize", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDevePermitirAcessoSemToken() throws Exception {
        mockMvc.perform(get("/cartoes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar400QuandoFormatoDeMesAnoForInvalido() throws Exception {
        mockMvc.perform(get("/cartoes/1234567812345678/fatura")
                        .header("Authorization", "Bearer " + token)
                        .param("mesAno", "12-2024")) // formato errado
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Formato inválido para o parâmetro 'mesAno'. Esperado: yyyy-MM"));
    }



}

