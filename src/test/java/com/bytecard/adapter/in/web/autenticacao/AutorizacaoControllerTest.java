package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.adapter.in.web.autenticacao.input.LoginRequest;
import com.bytecard.domain.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacaoControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AutorizacaoController autorizacaoController;

    @Test
    void deveAutenticarUsuarioERetornarToken() {
        LoginRequest request = new LoginRequest("admin@admin.com", "admin");

        var userDetails = User.builder()
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
}
