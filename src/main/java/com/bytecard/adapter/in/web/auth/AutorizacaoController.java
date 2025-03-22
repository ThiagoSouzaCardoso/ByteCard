package com.bytecard.adapter.in.web.auth;

import com.bytecard.adapter.in.web.auth.inputs.RegisterInput;
import com.bytecard.config.JwtService;
import com.bytecard.domain.port.in.cliente.ClienteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autorizacoes")
public class AutorizacaoController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ClienteUseCase clienteUseCase;


    public AutorizacaoController(AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 UserDetailsService userDetailsService,
                                 ClienteUseCase clienteUseCase) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.clienteUseCase = clienteUseCase;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails user = userDetailsService.loadUserByUsername(username);
        return jwtService.generateToken(user);
    }

    @PostMapping("/registros")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@Valid @RequestBody RegisterInput registerInput) {
        clienteUseCase.register(registerInput.toModel());
    }

}
