package com.bytecard.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

    class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
            SecurityContextHolder.clearContext();
        }


        @Test
    void deveIgnorarRequisicaoSemAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveIgnorarAuthorizationHeaderInvalido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

        @Test
        void deveAutenticarUsuarioComTokenValido() throws ServletException, IOException {
            String token = "valid.jwt.token";
            String username = "usuario";
            UserDetails userDetails = new User(username, "senha", Collections.emptyList());

            SecurityContextHolder.clearContext(); // <--- aqui está o segredo

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();

            assert authentication instanceof UsernamePasswordAuthenticationToken;
            assert authentication.getPrincipal().equals(userDetails);

            verify(filterChain).doFilter(request, response);
        }


    @Test
    void naoDeveAutenticarComTokenInvalido() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String username = "usuario";
        UserDetails userDetails = new User(username, "senha", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveIgnorarSeUsuarioJaAutenticado() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String username = "usuario";
        UserDetails userDetails = new User(username, "senha", Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveLancarExcecaoQuandoJwtFalhar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractUsername("token")).thenThrow(new RuntimeException("Erro interno"));

        assertThrows(RuntimeException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)
        );

        verify(jwtService).extractUsername("token");
        verifyNoMoreInteractions(jwtService, userDetailsService);
    }

        @Test
        void deveExecutarAutenticacaoQuandoUsernameNaoNuloESemAutenticacaoPrevia() throws Exception {
            String token = "token.jwt.valido";
            String username = "usuario";
            UserDetails userDetails = new User(username, "senha", Collections.emptyList());

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtService.validateToken(token, userDetails)).thenReturn(true);

            SecurityContextHolder.clearContext();

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assert SecurityContextHolder.getContext().getAuthentication() != null;
            assert SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(userDetails);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        void deveIgnorarQuandoUsernameForNulo() throws ServletException, IOException {
            String token = "Bearer token";

            when(request.getHeader("Authorization")).thenReturn(token);
            when(jwtService.extractUsername("token")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(jwtService).extractUsername("token");
            verify(filterChain).doFilter(request, response);
        }




    }

