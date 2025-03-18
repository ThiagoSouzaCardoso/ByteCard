package com.bytecard.adapter.in.web.auth.inputs;

public record RegisterInput(
        String nome,
        String cpf,
        String email,
        String senha
) {
}
