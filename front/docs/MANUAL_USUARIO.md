# Manual do usuário — Loja RV (Sistema de Gestão de Pedidos)

Este manual mostra como usar cada tela da interface. As imagens foram capturadas com a
API simulada, que já vem com produtos, clientes e pedidos de exemplo.

**Sumário:** [1. Navegação](#1-navegação) · [2. Catálogo](#2-catálogo-e-busca) ·
[3. Carrinho](#3-carrinho) · [4. Finalizar compra](#4-finalizar-compra-checkout) ·
[5. Pedidos](#5-acompanhar-pedidos) · [6. Gestão de produtos](#6-gestão-de-produtos-área-administrativa) ·
[7. Clientes](#7-clientes) · [8. Acessibilidade](#8-uso-pelo-teclado-e-leitor-de-tela)

---

## 1. Navegação

O menu superior leva a todas as áreas: **Catálogo**, **Pedidos**, **Clientes** e **Gestão de
produtos**. O ícone do carrinho mostra quantos itens ele tem. No celular, o menu fica atrás
do botão ☰ (canto superior direito).

| Celular                                             | Menu aberto                                 | Tablet                                        |
| --------------------------------------------------- | ------------------------------------------- | --------------------------------------------- |
| ![Catálogo no celular](img/25_celular_catalogo.png) | ![Menu no celular](img/26_celular_menu.png) | ![Pedido no tablet](img/28_tablet_pedido.png) |

Endereços que não existem mostram a página 404, com um botão para voltar ao catálogo.

![Página 404](img/22_pagina_404.png)

## 2. Catálogo e busca

A tela inicial lista os produtos ativos em cartões com categoria, situação de estoque e preço.

![Catálogo](img/01_catalogo.png)

Para filtrar, use os campos acima da grade. Os filtros se combinam e o resultado muda na hora:

- **Buscar por nome** — qualquer parte do nome (ex.: "livro");
- **Categoria** — Eletrônicos, Informática, Casa, Livros ou Esportes;
- **Preço mín./máx.** — faixa de preço em reais;
- **Limpar filtros** — volta a mostrar tudo.

![Filtros aplicados](img/02_catalogo_filtros.png)

Os filtros ficam no endereço da página, então dá para copiar o link e compartilhar a busca.

Clique no **nome** do produto para ver os detalhes. A etiqueta de estoque indica
_Em estoque_, _Últimas N unidades_ (5 ou menos) ou _Esgotado_. Escolha a quantidade
(limitada ao estoque disponível) e clique em **Adicionar ao carrinho**.

![Detalhe do produto](img/03_detalhe_produto.png)

Uma mensagem de confirmação aparece no canto da tela e o contador do carrinho é atualizado.

![Mensagem de produto adicionado](img/04_toast_adicionado.png)

## 3. Carrinho

![Carrinho](img/05_carrinho.png)

- Use **−** e **+** (ou digite) para mudar a quantidade; o máximo é o estoque do produto.
- **Remover** tira o item; **Esvaziar carrinho** tira todos.
- O resumo calcula **subtotal, frete e total** automaticamente. O frete é R$ 19,90 e fica
  **grátis a partir de R$ 200,00** — o resumo avisa quanto falta.
- O carrinho fica salvo no navegador: você pode trocar de tela ou recarregar a página sem perdê-lo.

Clique em **Finalizar compra** para ir ao checkout.

## 4. Finalizar compra (checkout)

A compra tem três etapas, mostradas no topo: **Entrega → Pagamento → Resultado**.

### 4.1 Entrega

Escolha o **cliente**; os endereços cadastrados dele aparecem para seleção (o primeiro já vem
marcado). Se o cliente ainda não existe, use **Cadastrar novo cliente**.

![Etapa de entrega](img/06_checkout_entrega.png)

### 4.2 Pagamento

Escolha a forma de pagamento:

| Forma                 | O que preencher                                                   | Resultado da simulação                                     |
| --------------------- | ----------------------------------------------------------------- | ---------------------------------------------------------- |
| **Cartão de crédito** | número, nome impresso, validade (MM/AA), CVV e parcelas (até 12x) | Aprovado. Cartões terminados em **0000** são **recusados** |
| **Pix**               | chave Pix (vem preenchida com o e-mail do cliente)                | Aprovado na hora                                           |
| **Boleto**            | nada — o vencimento é em 3 dias                                   | Pendente até a compensação                                 |

Campos inválidos são destacados em vermelho com a explicação logo abaixo, e o cursor vai para
o primeiro campo com problema. O erro some assim que o campo é corrigido.

| Validação                                                    | Preenchido                                       |
| ------------------------------------------------------------ | ------------------------------------------------ |
| ![Validação do cartão](img/07_checkout_validacao_cartao.png) | ![Cartão preenchido](img/08_checkout_cartao.png) |

Ao clicar em **Confirmar e pagar**, o pedido é criado (o estoque é reservado e o carrinho é
esvaziado) e o pagamento é enviado.

### 4.3 Resultado

- **Pagamento recusado** — o pedido continua pendente. Clique em **Tentar outra forma de
  pagamento** para pagar o mesmo pedido de outro jeito.

  ![Pagamento recusado](img/09_resultado_recusado.png)

  ![Nova tentativa com Pix](img/10_checkout_pix.png)

- **Pagamento aprovado** — o pedido passa para _Pago_.

  ![Pagamento aprovado](img/11_resultado_aprovado.png)

- **Pagamento pendente** (boleto) — o pedido fica _Pendente_ até alguém confirmar a
  compensação no detalhe do pedido.

Use **Acompanhar pedido** para abrir o pedido criado.

## 5. Acompanhar pedidos

A tela **Pedidos** lista todos os pedidos, do mais novo para o mais antigo. Os botões acima
da tabela filtram por situação e mostram quantos pedidos há em cada uma.

| Todos                                         | Filtro "Pendente"                                 |
| --------------------------------------------- | ------------------------------------------------- |
| ![Lista de pedidos](img/13_pedidos_lista.png) | ![Filtro por situação](img/14_pedidos_filtro.png) |

Em **Detalhes**, a **linha do tempo** mostra por quais situações o pedido já passou e quando:

![Detalhe do pedido](img/12_pedido_detalhe.png)

Os botões no quadro de situação avançam o pedido:

| Situação atual       | Ações disponíveis                                                                           |
| -------------------- | ------------------------------------------------------------------------------------------- |
| Pendente             | Cancelar pedido (devolve os itens ao estoque) · Confirmar compensação do boleto (só boleto) |
| Pago                 | Marcar como enviado                                                                         |
| Enviado              | Marcar como entregue                                                                        |
| Entregue / Cancelado | nenhuma (situação final)                                                                    |

Pedidos pagos não podem ser cancelados. O cancelamento sempre pede confirmação:

![Confirmar cancelamento](img/15_confirmar_cancelamento.png)

## 6. Gestão de produtos (área administrativa)

![Gestão de produtos](img/16_admin_produtos.png)

- **+ Novo produto** abre o cadastro; **Editar** abre o mesmo formulário preenchido.
- **Excluir** pede confirmação. Produtos que já aparecem em algum pedido **não podem ser
  excluídos** — nesse caso, edite o produto e desmarque **Produto ativo** para tirá-lo do catálogo.

![Confirmar exclusão](img/17_confirmar_exclusao.png)

No formulário, nome, categoria, preço (maior que zero) e estoque (inteiro, zero ou mais)
são obrigatórios. Não é permitido cadastrar dois produtos com o mesmo nome.

| Validação                                             | Edição                                          |
| ----------------------------------------------------- | ----------------------------------------------- |
| ![Validação do produto](img/18_produto_validacao.png) | ![Edição do produto](img/19_produto_edicao.png) |

## 7. Clientes

A lista permite buscar por nome, e-mail ou CPF.

![Clientes](img/20_clientes.png)

No cadastro, os campos de CPF, telefone e CEP recebem máscara automaticamente enquanto você
digita. As regras são:

- **E-mail** no formato `nome@dominio.com` e único no sistema;
- **CPF** válido (os dígitos verificadores são conferidos) e único;
- **Telefone** com DDD — fixo (10 dígitos) ou celular (11 dígitos, começando com 9);
- **CEP** com 8 dígitos, mais logradouro, número, bairro, cidade e UF.

Use **+ Adicionar outro endereço** para cadastrar mais de um endereço (ex.: casa e trabalho);
eles aparecem para escolha no checkout.

![Validação do cliente](img/21_cliente_validacao.png)

## 8. Uso pelo teclado e leitor de tela

- **Tab** percorre links, botões e campos; o item em foco ganha um contorno bem visível.
- O primeiro **Tab** da página mostra **Pular para o conteúdo**, que leva direto ao conteúdo
  principal sem passar pelo menu.
- **Esc** fecha as janelas de confirmação; enquanto uma está aberta, o **Tab** fica dentro dela.
- Mensagens de sucesso e erro, totais do carrinho e quantidade de resultados são anunciados
  pelos leitores de tela automaticamente.

| Pular para o conteúdo                                    | Foco visível no menu                          |
| -------------------------------------------------------- | --------------------------------------------- |
| ![Pular para o conteúdo](img/23_foco_pular_conteudo.png) | ![Foco no menu](img/24_foco_visivel_menu.png) |
