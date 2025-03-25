package com.bytecard.domain.service;

import com.bytecard.adapter.out.persistence.cliente.entity.ClienteEntity;
import com.bytecard.adapter.out.persistence.cliente.repository.ClienteRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private ClienteRespository clienteRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRespository.class);
        userDetailsService = new UserDetailsServiceImpl(clienteRepository);
    }

    @Test
    void deveRetornarUserDetailsQuandoEmailExiste() {
        // Arrange
        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmail("usuario@email.com");
        cliente.setSenha("senhaCodificada");
        cliente.setRole("ROLE_ADMIN");

        when(clienteRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(cliente));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario@email.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("usuario@email.com");
        assertThat(userDetails.getPassword()).isEqualTo("senhaCodificada");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoEmailNaoExiste() {
        // Arrange
        when(clienteRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("inexistente@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: inexistente@email.com");
    }

    @Test
    void deveRemoverPrefixoROLESePresente() {
        // Arrange
        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmail("teste@email.com");
        cliente.setSenha("senhaSegura");
        cliente.setRole("ROLE_USER");

        when(clienteRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(cliente));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@email.com");

        // Assert
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void deveAceitarRoleSemPrefixoROLE() {
        // Arrange
        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmail("raw@email.com");
        cliente.setSenha("abc123");
        cliente.setRole("ADMIN"); // sem ROLE_

        when(clienteRepository.findByEmail("raw@email.com")).thenReturn(Optional.of(cliente));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("raw@email.com");

        // Assert
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }
}

