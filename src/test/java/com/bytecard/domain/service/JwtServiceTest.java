package com.bytecard.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService - Testes Unitários")
class JwtServiceTest {

    private JwtService jwtService;
    private String secret;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secret = Encoders.BASE64.encode(key.getEncoded());

        Field field = JwtService.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(jwtService, secret);
    }

    @Test
    @DisplayName("Deve gerar token válido com username e roles")
    void deveGerarTokenValido() {
        UserDetails user = new User("usuario", "senha", List.of(
                new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("usuario");
    }

    @Test
    @DisplayName("Deve extrair username corretamente")
    void deveExtrairUsername() {
        UserDetails user = new User("admin", "senha", Collections.emptyList());
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("admin");
    }

    @Test
    @DisplayName("Deve extrair claims corretamente")
    void deveExtrairClaims() {
        UserDetails user = new User("claims", "senha", List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractClaim(token, c -> c);
        assertThat(claims.getSubject()).isEqualTo("claims");
        assertThat(claims.get("roles")).isInstanceOf(List.class);
    }

}
