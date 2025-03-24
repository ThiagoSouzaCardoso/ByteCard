package com.bytecard.adapter.in.web.cliente.inputs;

import com.bytecard.domain.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NovoClienteRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String senha,

        @NotBlank(message = "Papel é obrigatório")
        @Pattern(
                regexp = "ADMIN|CLIENTE|GERENTE",
                message = "Papel deve ser um dos seguintes: ADMIN, CLIENTE ou GERENTE"
        )
        String papel
) {

   public Cliente toModel(){
        return Cliente.builder()
                .email(this.email())
                .senha(this.senha())
                .cpf(this.cpf())
                .nome(this.nome())
                .papel(this.papel())
                .build();
    }


}
