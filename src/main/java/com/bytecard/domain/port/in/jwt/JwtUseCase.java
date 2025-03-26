package com.bytecard.domain.port.in.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtUseCase {

    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

     <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

   // boolean validateToken(String token, UserDetails userDetails);

     boolean isTokenExpired(String token);


    }
