-- Schema do banco "ecommerce" (fonte da verdade - Aulas 12, 13 e 15)
-- Rodar uma vez no MySQL antes do mvn spring-boot:run:
--   mysql -u root -p < src/main/resources/db/schema.sql
-- (opcional) dados de exemplo para a interface:
--   mysql -u root -p < src/main/resources/db/dados-exemplo.sql
--
-- ATENÇÃO: quem já criou o banco na Aula 14 precisa recriá-lo, porque
-- "CREATE TABLE IF NOT EXISTS" não acrescenta as colunas novas da Aula 15:
--   mysql -u root -p -e "DROP DATABASE ecommerce"

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
    descricao   VARCHAR(300)   NULL,
    categoria   VARCHAR(60)    NOT NULL DEFAULT 'Geral',
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
    telefone VARCHAR(20) NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uk_cliente_cpf UNIQUE (cpf),
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

-- Aula 15 - Composição cliente -> endereço: CASCADE (o endereço não existe sem o cliente)
CREATE TABLE IF NOT EXISTS endereco (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id   BIGINT       NOT NULL,
    apelido      VARCHAR(40)  NULL,
    cep          VARCHAR(9)   NOT NULL,
    logradouro   VARCHAR(120) NOT NULL,
    numero       VARCHAR(20)  NOT NULL,
    complemento  VARCHAR(60)  NULL,
    bairro       VARCHAR(60)  NOT NULL,
    cidade       VARCHAR(60)  NOT NULL,
    uf           VARCHAR(2)   NOT NULL,
    CONSTRAINT pk_endereco PRIMARY KEY (id),
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE CASCADE
);

-- Associação pedido -> cliente: RESTRICT (não se apaga cliente que tem pedido)
CREATE TABLE IF NOT EXISTS pedido (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    cliente_id    BIGINT      NOT NULL,
    situacao      VARCHAR(20) NOT NULL,
    data_criacao  DATETIME(6) NOT NULL,
    -- Aula 15: "foto" do endereço de entrega (EnderecoEntrega, @Embedded)
    entrega_cep          VARCHAR(9)   NULL,
    entrega_logradouro   VARCHAR(120) NULL,
    entrega_numero       VARCHAR(20)  NULL,
    entrega_complemento  VARCHAR(60)  NULL,
    entrega_bairro       VARCHAR(60)  NULL,
    entrega_cidade       VARCHAR(60)  NULL,
    entrega_uf           VARCHAR(2)   NULL,
    -- Aula 15: última tentativa de pagamento (RegistroPagamento, @Embedded)
    pagamento_forma        VARCHAR(10)  NULL,
    pagamento_resultado    VARCHAR(10)  NULL,
    pagamento_comprovante  VARCHAR(40)  NULL,
    pagamento_descricao    VARCHAR(120) NULL,
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

-- Aula 15 - Linha do tempo do pedido (@ElementCollection de RegistroSituacao)
CREATE TABLE IF NOT EXISTS pedido_historico (
    pedido_id  BIGINT      NOT NULL,
    situacao   VARCHAR(20) NOT NULL,
    data_registro  DATETIME(6) NOT NULL,
    CONSTRAINT fk_historico_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedido (id) ON DELETE CASCADE
);
