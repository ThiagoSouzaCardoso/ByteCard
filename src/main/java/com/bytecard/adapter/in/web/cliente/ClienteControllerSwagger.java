package com.bytecard.adapter.in.web.cliente;

import com.bytecard.adapter.in.web.cliente.inputs.NovoClienteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Clientes", description = "Cadastro e gerenciamento de clientes")
@SecurityRequirement(name = "Bearer Token")
@RequestMapping("/clientes")
public interface ClienteControllerSwagger {

    @Operation(
            summary = "Registrar novo cliente",
            description = "Cadastra um novo cliente no sistema com nome, CPF, e-mail, senha e papel (role).",
            requestBody = @RequestBody(
                    description = "Dados do novo cliente a ser registrado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NovoClienteRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente cadastrado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos (Bean Validation)"),
                    @ApiResponse(responseCode = "409", description = "Cliente com CPF ou e-mail já existe")
            }
    )
    void registerUser(NovoClienteRequest novoClienteRequest);
}

