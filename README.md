# 📘 Documentação da API - ByteCard

Esta API permite o gerenciamento de cartões de crédito, autenticação com JWT, transações e geração de relatórios por categoria.

---

## 🔐 Autenticação via JWT

1. Faça login via `/autorizacoes/login` com:
   ```json
   {
     "username": "usuario@usuario.com",
     "password": "senhaSegura123"
   }
   ```

2. Copie o token JWT retornado e clique em "Authorize" no Swagger UI.

3. Cole o token no formato:

   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
   ```

---

## 🚀 Endpoints principais

- **POST /autorizacoes/login** → autenticação
- **POST /cartoes** → cadastro de cartão
- **PATCH /cartoes/{numero}/ativar** → ativar cartão
- **PATCH /cartoes/{numero}/cancelar** → cancelar cartão
- **GET /cartoes** → listar cartões com filtros
- **GET /cartoes/{numero}/fatura?mesAno=YYYY-MM** → fatura do cartão
- **POST /relatorios** → relatório de gastos por categoria

---

## 📂 Swagger UI

Acesse a interface interativa em:

```
http://localhost:8080/swagger-ui.html
```

---

## 📥 Esquema OpenAPI

Você pode usar o arquivo `openapi.yaml` para importar no [Swagger Editor](https://editor.swagger.io) ou gerar clientes com o OpenAPI Generator.

---

## 🛠️ Requisitos de segurança

Todos os endpoints (exceto `/autorizacoes/login`) exigem o uso de token JWT via header `Authorization`.