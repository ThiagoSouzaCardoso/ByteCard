package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.domain.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutorizacaoController - Unit")
class AutorizacaoControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks private AutorizacaoController autorizacaoController;

    @Nested
    @DisplayName("Login")
    class LoginTests {

        @Test
        @DisplayName("Deve autenticar com sucesso e retornar token JWT")
        void deveAutenticarUsuarioERetornarToken() {
            LoginRequest request = new LoginRequest("admin@admin.com", "admin");

            UserDetails userDetails = User.builder()
                    .username("admin@admin.com")
                    .password("senha123")
                    .roles("ADMIN")
                    .build();

            when(userDetailsService.loadUserByUsername("admin@admin.com")).thenReturn(userDetails);
            when(jwtService.generateToken(userDetails)).thenReturn("fake-jwt-token");

            var response = autorizacaoController.login(request);

            verify(authenticationManager).authenticate(
                    new UsernamePasswordAuthenticationToken("admin@admin.com", "admin")
            );
            verify(jwtService).generateToken(userDetails);
            assertThat(response.token()).isEqualTo("fake-jwt-token");
        }

        @Test
        @DisplayName("Deve lançar exceção ao falhar autenticação")
        void deveLancarExcecaoQuandoCredenciaisInvalidas() {
            LoginRequest request = new LoginRequest("admin@admin.com", "senhaErrada");

            doThrow(new BadCredentialsException("Credenciais inválidas"))
                    .when(authenticationManager)
                    .authenticate(any());

            assertThatThrownBy(() -> autorizacaoController.login(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Credenciais inválidas");

            verify(authenticationManager).authenticate(
                    new UsernamePasswordAuthenticationToken("admin@admin.com", "senhaErrada")
            );
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("Deve lançar exceção se usuário não for encontrado")
        void deveLancarExcecaoSeUsuarioNaoExistir() {
            LoginRequest request = new LoginRequest("naoexiste@email.com", "senha");

            when(userDetailsService.loadUserByUsername("naoexiste@email.com"))
                    .thenThrow(new RuntimeException("Usuário não encontrado"));

            assertThatThrownBy(() -> autorizacaoController.login(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuário não encontrado");
        }
    }
}