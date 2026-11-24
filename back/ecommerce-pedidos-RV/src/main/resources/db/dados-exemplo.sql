-- Dados de exemplo para demonstrar a interface (rodar DEPOIS do schema.sql):
--   mysql -u root -p < src/main/resources/db/dados-exemplo.sql
-- Os pedidos são criados pela própria interface (checkout).

USE ecommerce;

INSERT INTO produto (nome, descricao, categoria, preco, quantidade, ativo) VALUES
 ('Fone Bluetooth ProSound', 'Fone sem fio com cancelamento de ruído e 30 h de bateria.', 'Eletrônicos', 349.90, 25, 1),
 ('Smartwatch Fit 2', 'Monitor cardíaco, GPS integrado e resistência à água.', 'Eletrônicos', 599.00, 3, 1),
 ('Teclado Mecânico RGB', 'Switches táteis, layout ABNT2 e iluminação configurável.', 'Informática', 289.90, 14, 1),
 ('Mouse Sem Fio Ergo', 'Design ergonômico, 6 botões e sensor de 4000 DPI.', 'Informática', 129.90, 40, 1),
 ('Monitor 27" QHD', 'Painel IPS 2560x1440, 75 Hz, ajuste de altura.', 'Informática', 1599.00, 0, 1),
 ('Cafeteira Expresso Barista', 'Pressão de 15 bar e vaporizador para leite.', 'Casa', 799.90, 8, 1),
 ('Jogo de Panelas Antiaderente', 'Cinco peças com revestimento cerâmico.', 'Casa', 459.00, 12, 1),
 ('Livro Código Limpo', 'Clássico sobre boas práticas de programação.', 'Livros', 89.90, 30, 1),
 ('Livro Entendendo Algoritmos', 'Guia ilustrado para iniciantes em algoritmos.', 'Livros', 64.90, 2, 1),
 ('Bola de Futebol Oficial', 'Tamanho 5, costurada à máquina.', 'Esportes', 119.90, 20, 1),
 ('Tapete de Yoga Premium', 'Material antiderrapante, 6 mm de espessura.', 'Esportes', 149.00, 16, 1),
 ('Caixa de Som Portátil', 'Modelo descontinuado (exclusão lógica: ativo = 0).', 'Eletrônicos', 199.90, 5, 0);

INSERT INTO cliente (nome, cpf, email, telefone) VALUES
 ('Ana Souza', '529.982.247-25', 'ana.souza@email.com', '(16) 99123-4567'),
 ('Bruno Lima', '111.444.777-35', 'bruno.lima@email.com', '(16) 3371-2233'),
 ('Carla Mendes', '123.456.789-09', 'carla.mendes@email.com', '(11) 98888-7777');

INSERT INTO endereco (cliente_id, apelido, cep, logradouro, numero, complemento, bairro, cidade, uf) VALUES
 ((SELECT id FROM cliente WHERE email = 'ana.souza@email.com'), 'Casa', '13560-000', 'Rua Episcopal', '1200', 'Apto 12', 'Centro', 'São Carlos', 'SP'),
 ((SELECT id FROM cliente WHERE email = 'ana.souza@email.com'), 'Trabalho', '13566-590', 'Av. Trabalhador São-carlense', '400', NULL, 'Parque Arnold Schimidt', 'São Carlos', 'SP'),
 ((SELECT id FROM cliente WHERE email = 'bruno.lima@email.com'), 'Casa', '14801-020', 'Rua Voluntários da Pátria', '85', NULL, 'Centro', 'Araraquara', 'SP'),
 ((SELECT id FROM cliente WHERE email = 'carla.mendes@email.com'), 'Casa', '01310-100', 'Av. Paulista', '1578', 'Bloco B', 'Bela Vista', 'São Paulo', 'SP');
