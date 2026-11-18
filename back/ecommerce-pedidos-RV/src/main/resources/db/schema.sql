-- Schema do banco "ecommerce" (fonte da verdade - Aulas 12 e 13)
-- Rodar uma vez no MySQL antes do mvn spring-boot:run:
--   mysql -u root -p < src/main/resources/db/schema.sql

CREATE DATABASE IF NOT EXISTS ecommerce
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ecommerce;

CREATE TABLE IF NOT EXISTS produto (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(120)   NOT NULL,
    preco       DECIMAL(10, 2) NOT NULL,
    quantidade  INT            NOT NULL,
    ativo       BIT(1)         NOT NULL DEFAULT 1,
    CONSTRAINT pk_produto PRIMARY KEY (id),
    CONSTRAINT uk_produto_nome UNIQUE (nome),
    CONSTRAINT ck_produto_preco CHECK (preco > 0),
    CONSTRAINT ck_produto_quantidade CHECK (quantidade >= 0)
);

CREATE TABLE IF NOT EXISTS cliente (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nome   VARCHAR(120) NOT NULL,
    cpf    VARCHAR(14)  NOT NULL,
    email  VARCHAR(120) NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uk_cliente_cpf UNIQUE (cpf),
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

-- Associação pedido -> cliente: RESTRICT (não se apaga cliente que tem pedido)
CREATE TABLE IF NOT EXISTS pedido (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    cliente_id    BIGINT      NOT NULL,
    situacao      VARCHAR(20) NOT NULL,
    data_criacao  DATETIME(6) NOT NULL,
    CONSTRAINT pk_pedido PRIMARY KEY (id),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT
);

-- Composição pedido -> item: CASCADE (o item não existe sem o pedido)
-- Item -> produto: RESTRICT (produto que já foi vendido não pode ser apagado)
CREATE TABLE IF NOT EXISTS item_pedido (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    pedido_id       BIGINT         NOT NULL,
    produto_id      BIGINT         NOT NULL,
    quantidade      INT            NOT NULL,
    preco_unitario  DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_item_pedido PRIMARY KEY (id),
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedido (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id)
        REFERENCES produto (id) ON DELETE RESTRICT,
    CONSTRAINT ck_item_quantidade CHECK (quantidade > 0)
);
