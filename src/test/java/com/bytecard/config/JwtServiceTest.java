package com.bytecard.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secret = Encoders.BASE64.encode(key.getEncoded());

        try {
            var field = JwtService.class.getDeclaredField("secret");
            field.setAccessible(true);
            field.set(jwtService, secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deveGerarTokenValido() {
        UserDetails user = new User("usuario", "senha", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo("usuario");
        assertThat(jwtService.validateToken(token, user)).isTrue();
    }

    @Test
    void deveExtrairClaimsCorretamente() {
        UserDetails user = new User("teste", "senha", Collections.emptyList());
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractClaim(token, c -> c);
        assertThat(claims.getSubject()).isEqualTo("teste");
        assertThat(claims.get("roles")).isInstanceOf(List.class);
    }

    @Test
    void deveDetectarTokenExpirado() {
        String expiredToken = Jwts.builder()
                .subject("expired")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("expired", "senha", Collections.emptyList());

        boolean valido = jwtService.validateToken(expiredToken, user);
        assertThat(valido).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoUsernameForDiferente() {
        String token = jwtService.generateToken(new User("usuario1", "senha", List.of()));
        UserDetails user = new User("usuario2", "senha", List.of());

        boolean valido = jwtService.validateToken(token, user);

        assertThat(valido).isFalse();
    }

    @Test
    void deveValidarTokenComSucessoQuandoNaoExpiradoEUsernameCorreto() {
        UserDetails user = new User("usuario", "senha", Collections.emptyList());

        String token = Jwts.builder()
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        boolean valido = jwtService.validateToken(token, user);

        assertThat(valido).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoTokenForMalformado() {
        String malformedToken = "abc.def.ghi"; // Token não assinado corretamente
        UserDetails user = new User("usuario", "senha", Collections.emptyList());

        boolean valido = jwtService.validateToken(malformedToken, user);
        assertThat(valido).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoTokenEstiverComAssinaturaInvalida() {
        SecretKey outraChave = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String outroSecret = Encoders.BASE64.encode(outraChave.getEncoded());

        String tokenAssinadoDiferente = Jwts.builder()
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(outroSecret)))
                .compact();

        UserDetails user = new User("usuario", "senha", Collections.emptyList());

        boolean valido = jwtService.validateToken(tokenAssinadoDiferente, user);
        assertThat(valido).isFalse();
    }

    @Test
    void deveValidarTokenComSucesso() {
        UserDetails user = new User("usuario", "senha", Collections.emptyList());
        String token = jwtService.generateToken(user);

        boolean valido = jwtService.validateToken(token, user);

        assertThat(valido).isTrue();
    }

    @Test
    void deveChegarNaVerificacaoDeExpiracaoDoToken() {
        UserDetails user = new User("usuario", "senha", Collections.emptyList());
        String token = jwtService.generateToken(user);

        boolean valid = jwtService.validateToken(token, user);

        assertThat(valid).isTrue();
    }

    // Verifica cada combinação lógica

    @Test
    void usernameIgualETokenValido() {
        UserDetails user = new User("usuario", "senha", List.of());
        String token = jwtService.generateToken(user);
        assertThat(jwtService.validateToken(token, user)).isTrue();
    }

    @Test
    void usernameIgualETokenExpirado() {
        String token = Jwts.builder()
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }

    @Test
    void usernameDiferenteETokenValido() {
        String token = Jwts.builder()
                .subject("usuario1")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario2", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }

    @Test
    void usernameDiferenteETokenExpirado() {
        String token = Jwts.builder()
                .subject("usuario1")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario2", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }

    @Test
    void usernameIgualEtokenExpirado() {
        String token = Jwts.builder()
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }

    @Test
    void usernameIgualEtokenValido() {
        String token = Jwts.builder()
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isTrue();
    }


    @Test
    void usernameDiferenteEtokenExpirado() {
        String token = Jwts.builder()
                .subject("outro")
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }


    @Test
    void usernameDiferenteEtokenValido() {
        String token = Jwts.builder()
                .subject("outro")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        UserDetails user = new User("usuario", "senha", List.of());
        assertThat(jwtService.validateToken(token, user)).isFalse();
    }


}