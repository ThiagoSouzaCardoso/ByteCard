package com.bytecard.config;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClienteRespository usuarioRepository;

    public UserDetailsServiceImpl(ClienteRespository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ClienteEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRole().replace("ROLE_", ""))
                .build();
    }
}

