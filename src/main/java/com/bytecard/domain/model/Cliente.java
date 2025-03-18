package com.bytecard.domain.model;

import lombok.Builder;

@Builder
public record Cliente(
     String nome,
     String cpf,
     String email,
     String senha
){
}
