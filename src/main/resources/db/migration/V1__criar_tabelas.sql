-- Criando a tabela CLIENTE
CREATE TABLE cliente_entity (
                                id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                cpf        VARCHAR(11)  NOT NULL UNIQUE,
                                email      VARCHAR(255) NOT NULL UNIQUE,
                                nome       VARCHAR(255) NOT NULL,
                                role       VARCHAR(50)  NOT NULL,
                                senha      VARCHAR(255) NOT NULL
);

-- Criando a tabela CARTÃO
CREATE TABLE cartao_entity (
                               id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                               numero      VARCHAR(16) NOT NULL UNIQUE,
                               cvv         VARCHAR(3)  NOT NULL,
                               limite      DECIMAL(10,2) NOT NULL DEFAULT 0,
                               status      VARCHAR(20) CHECK (status IN ('ATIVO', 'BLOQUEADO', 'CANCELADO')),
                               validade    VARCHAR(7) NOT NULL,
                               cliente_id  BIGINT NOT NULL,
                               FOREIGN KEY (cliente_id) REFERENCES cliente_entity(id) ON DELETE CASCADE
);

-- Criando a tabela TRANSAÇÃO
CREATE TABLE transacao_entity (
                                  id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                  data_hora  TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  valor      DECIMAL(10,2) NOT NULL CHECK (valor > 0),
                                  categoria VARCHAR(50) NOT NULL CHECK (categoria IN ('ALIMENTACAO', 'SAUDE', 'LAZER', 'CASA', 'TRANSPORTE', 'EDUCACAO', 'OUTROS')),
                                  estabelecimento VARCHAR(255) NOT NULL,
                                  cartao_id  BIGINT NOT NULL,
                                  FOREIGN KEY (cartao_id) REFERENCES cartao_entity(id) ON DELETE CASCADE
);
