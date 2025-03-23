package com.bytecard.adapter.in.web.autenticacao;

import com.bytecard.config.JwtService;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autorizacoes")
public class AutorizacaoController implements AutorizacaoControllerSwagger{

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AutorizacaoController(AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 UserDetailsService userDetailsService,
                                 ClienteUseCase clienteUseCase) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails user = userDetailsService.loadUserByUsername(username);
        return jwtService.generateToken(user);
    }

}
