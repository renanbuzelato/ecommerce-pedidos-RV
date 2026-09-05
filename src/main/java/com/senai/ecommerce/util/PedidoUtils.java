package com.senai.ecommerce.util;

import java.util.Random;
import java.time.Year;

/**
 * Classe utilitária que centraliza as regras de negócio de cálculos, formatações
 * e validações do módulo de gestão de pedidos do sistema de e-commerce.
 * <p>
 * Aplica boas práticas de Clean Code, encapsulamento rígido (métodos estáticos e 
 * construtor privado) e prevenção de erros por meio de Javadoc completo e validações defensivas.
 * </p>
 * 
 * @author Squad Backend
 * @version 1.0
 */
public final class PedidoUtils {

    // ── CONSTANTES DE REGRA DE NEGÓCIO (Nenhum número mágico no código) ──────────────────
    
    /** Valor mínimo aceito para preços, subtotais, pesos e descontos. */
    private static final double LIMITE_MINIMO_MONETARIO = 0.0;
    
    /** Quantidade mínima aceitável para qualquer item do carrinho. */
    private static final int LIMITE_MINIMO_QUANTIDADE = 0;
    
    /** Valor percentual máximo aceitável para um desconto (100%). */
    private static final double PERCENTUAL_MAXIMO_DESCONTO = 100.0;
    
    /** Máximo número aleatório gerado para compor o identificador do pedido (5 dígitos). */
    private static final int LIMITE_RANDOM_NUMERO_PEDIDO = 100000;
    
    /** Formato padrão do código identificador do pedido. */
    private static final String FORMATO_CODIGO_PEDIDO = "PED-%d-%05d";
    
    /** Formato padrão da linha de cabeçalho do recibo. */
    private static final String CABECALHO_RECIBO = "%-25s | %10s | %4s | %12s";
    
    /** Formato padrão das linhas de produtos no recibo. */
    private static final String LAYOUT_LINHA_ITEM = "%-25.25s | %10.2f | %4d | %12.2f";
    
    /** Separador estético para as seções do recibo impresso. */
    private static final String DIVISOR_RECIBO = "-------------------------------------------------------------";

    /**
     * Construtor privado para evitar a instanciação acidental desta classe utilitária.
     * 
     * @throws IllegalStateException se houver tentativa de instanciar via reflection.
     */
    private PedidoUtils() {
        throw new IllegalStateException("Esta é uma classe utilitária e não pode ser instanciada.");
    }

    // ── MÉTODOS OBRIGATÓRIOS ────────────────────────────────────────────────────────────

    /**
     * Gera um identificador único de pedido no padrão PED-AAAA-NNNNN.
     * <p>
     * Onde AAAA representa o ano corrente dinâmico do sistema e NNNNN é um 
     * número aleatório de 5 dígitos (preenchido com zeros à esquerda se necessário).
     * </p>
     * 
     * @return String contendo o número formatado do pedido.
     */
    public static String gerarNumeroDoPedido() {
        Random random = new Random();
        int anoCorrente = Year.now().getValue();
        int numeroAleatorio = random.nextInt(LIMITE_RANDOM_NUMERO_PEDIDO);
        
        return String.format(FORMATO_CODIGO_PEDIDO, anoCorrente, numeroAleatorio);
    }

    /**
     * Calcula o valor subtotal do pedido com base nos vetores de preços e quantidades.
     * 
     * @param precos Vetor de double contendo os preços unitários de cada item.
     * @param quantidades Vetor de int contendo as respectivas quantidades pedidas.
     * @return O subtotal resultante da somatória do produto de preço por quantidade.
     * @throws IllegalArgumentException se os vetores forem nulos, vazios, tiverem tamanhos 
     *                                  divergentes ou se contiverem valores inválidos (valores negativos).
     */
    public static double calcularSubtotal(double[] precos, int[] quantidades) {
        validarVetoresItens(precos, quantidades);
        
        double subtotal = LIMITE_MINIMO_MONETARIO;
        for (int i = 0; i < precos.length; i++) {
            if (precos[i] < LIMITE_MINIMO_MONETARIO) {
                throw new IllegalArgumentException("Erro: O preço unitário do item " + i + " não pode ser negativo.");
            }
            if (quantidades[i] <= LIMITE_MINIMO_QUANTIDADE) {
                throw new IllegalArgumentException("Erro: A quantidade do item " + i + " deve ser maior que zero.");
            }
            subtotal += precos[i] * quantidades[i];
        }
        
        return subtotal;
    }

    /**
     * Calcula o valor do frete com base nas regras de peso e faixas de frete grátis.
     * <p>
     * O frete cobra por quilo iniciado (arredondado para cima usando Math.ceil),
     * garante a cobrança de um valor mínimo (usando Math.max) e zera integralmente
     * se o subtotal atingir o valor limite para frete grátis.
     * </p>
     * 
     * @param pesoTotal O peso acumulado total de todas as mercadorias em quilos (kg).
     * @param precoPorQuilo Taxa monetária cobrada por quilo transportado.
     * @param freteMinimo O limite mínimo a ser cobrado se o valor por peso for muito baixo.
     * @param valorFreteGratis O limite do subtotal de compras que isenta a cobrança de frete.
     * @param valorSubtotal O subtotal real do pedido do cliente.
     * @return O valor final calculado para o frete do pedido.
     * @throws IllegalArgumentException se qualquer parâmetro numérico for inválido (valores negativos ou peso zero).
     */
    public static double calcularFrete(double pesoTotal, double precoPorQuilo, double freteMinimo, 
                                       double valorFreteGratis, double valorSubtotal) {
        
        if (pesoTotal <= LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O peso total do pedido deve ser maior que zero.");
        }
        if (precoPorQuilo < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O preço por quilo não pode ser negativo.");
        }
        if (freteMinimo < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O frete mínimo não pode ser negativo.");
        }
        if (valorFreteGratis < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O limiar de frete grátis não pode ser negativo.");
        }
        if (valorSubtotal < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O subtotal do pedido não pode ser negativo.");
        }

        // Se o subtotal atingir a meta, o frete é totalmente isento
        if (valorSubtotal >= valorFreteGratis) {
            return LIMITE_MINIMO_MONETARIO;
        }

        // Arredonda o peso total para cima para cobrar por "quilo iniciado"
        double pesoArredondado = Math.ceil(pesoTotal);
        double custoPorPeso = pesoArredondado * precoPorQuilo;

        // Garante que o valor final respeite a cobrança do frete mínimo estabelecido
        return Math.max(custoPorPeso, freteMinimo);
    }

    /**
     * Calcula o desconto aplicável respeitando o teto máximo absoluto.
     * 
     * @param valorSubtotal O subtotal acumulado do pedido de compra.
     * @param percentualDesconto Alíquota percentual do desconto (Ex: 15.0 para 15%).
     * @param tetoDesconto O valor monetário máximo que este desconto pode conceder.
     * @return O valor do desconto a ser subtraído do pedido.
     * @throws IllegalArgumentException se o subtotal ou teto forem negativos, ou se a taxa 
     *                                  estiver fora do intervalo operacional [0%, 100%].
     */
    public static double calcularDesconto(double valorSubtotal, double percentualDesconto, double tetoDesconto) {
        if (valorSubtotal < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O valor subtotal não pode ser negativo.");
        }
        if (percentualDesconto < LIMITE_MINIMO_MONETARIO || percentualDesconto > PERCENTUAL_MAXIMO_DESCONTO) {
            throw new IllegalArgumentException("Erro: O percentual de desconto deve estar entre 0.0% e 100.0%.");
        }
        if (tetoDesconto < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O valor do teto de desconto não pode ser negativo.");
        }

        double descontoCalculado = valorSubtotal * (percentualDesconto / PERCENTUAL_MAXIMO_DESCONTO);
        
        // Garante que o desconto calculado não ultrapasse o teto máximo parametrizado
        return Math.min(descontoCalculado, tetoDesconto);
    }

    /**
     * Formata uma linha detalhada de produto para exibição tabular em recibos.
     * 
     * @param nomeProduto Nome ou descrição curta do item de venda.
     * @param precoUnitario Preço individual do produto.
     * @param quantidade Volume adquirido deste produto.
     * @param totalItem Valor acumulado resultante (precoUnitario * quantidade).
     * @return String alinhada em colunas estéticas para simetria do recibo.
     * @throws IllegalArgumentException se o nome do produto for inválido ou se valores numéricos forem negativos.
     */
    public static String formatarLinhaDoRecibo(String nomeProduto, double precoUnitario, int quantidade, double totalItem) {
        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do produto não pode ser nulo ou vazio.");
        }
        if (precoUnitario < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O preço unitário do item não pode ser negativo.");
        }
        if (quantidade <= LIMITE_MINIMO_QUANTIDADE) {
            throw new IllegalArgumentException("Erro: A quantidade do item deve ser maior que zero.");
        }
        if (totalItem < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: O valor total do item não pode ser negativo.");
        }

        return String.format(LAYOUT_LINHA_ITEM, nomeProduto.trim(), precoUnitario, quantidade, totalItem);
    }

    // ── REQUISITOS DESEJÁVEIS ──────────────────────────────────────────────────────────

    /**
     * Normaliza e higieniza o nome de um cliente, removendo espaços excedentes e
     * aplicando o padrão de caixa "Title Case" (Iniciais Maiúsculas, restante minúsculas).
     * 
     * @param nome O nome bruto fornecido pelo usuário.
     * @return O nome perfeitamente higienizado e padronizado.
     * @throws IllegalArgumentException se o nome passado for nulo ou puramente vazio.
     */
    public static String normalizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do cliente não pode ser nulo ou vazio.");
        }

        // Divide o nome utilizando expressões regulares para capturar múltiplos espaços em branco
        String[] partes = nome.trim().split("\s+");
        StringBuilder nomeNormalizado = new StringBuilder();

        for (int i = 0; i < partes.length; i++) {
            String palavra = partes[i].toLowerCase();
            if (!palavra.isEmpty()) {
                // Capitaliza apenas a primeira letra de cada palavra
                String palavraCapitalizada = Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
                nomeNormalizado.append(palavraCapitalizada);
                
                // Insere espaço entre as partes, exceto após a última palavra
                if (i < partes.length - 1) {
                    nomeNormalizado.append(" ");
                }
            }
        }

        return nomeNormalizado.toString();
    }

    /**
     * Monta o recibo de compra completo e formatado de forma tabular.
     * <p>
     * Utiliza internamente StringBuilder para melhor performance de concatenação 
     * e o método formatarLinhaDoRecibo para padronizar e estruturar os itens.
     * </p>
     * 
     * @param nomeCliente Nome do comprador.
     * @param produtos Vetor contendo a descrição de todos os produtos do carrinho.
     * @param precos Vetor de preços unitários correspondentes.
     * @param quantidades Vetor de quantidades adquiridas.
     * @param subtotal O subtotal acumulado das mercadorias.
     * @param frete A taxa cobrada de transporte.
     * @param desconto O desconto total aplicado sobre o subtotal.
     * @param total O valor final real a ser cobrado do cliente.
     * @return O texto completo do recibo pronto para impressão ou log.
     * @throws IllegalArgumentException se houver inconsistência nos dados dos itens ou valores negativos.
     */
    public static String montarRecibo(String nomeCliente, String[] produtos, double[] precos, int[] quantidades,
                                      double subtotal, double frete, double desconto, double total) {
        
        if (produtos == null || produtos.length == 0) {
            throw new IllegalArgumentException("Erro: O recibo deve conter pelo menos um produto.");
        }
        validarVetoresItens(precos, quantidades);
        if (produtos.length != precos.length) {
            throw new IllegalArgumentException("Erro: O tamanho do vetor de produtos deve ser idêntico ao de preços.");
        }
        if (subtotal < LIMITE_MINIMO_MONETARIO || frete < LIMITE_MINIMO_MONETARIO || 
            desconto < LIMITE_MINIMO_MONETARIO || total < LIMITE_MINIMO_MONETARIO) {
            throw new IllegalArgumentException("Erro: Valores financeiros do recibo não podem ser negativos.");
        }

        StringBuilder recibo = new StringBuilder();
        String nomeHigienizado = normalizarNome(nomeCliente);
        String codigoPedido = gerarNumeroDoPedido();

        recibo.append(DIVISOR_RECIBO).append("
");
        recibo.append("              RECIBO DE GESTÃO DE PEDIDOS - SENAI
");
        recibo.append(DIVISOR_RECIBO).append("
");
        recibo.append(String.format("Código do Pedido: %s
", codigoPedido));
        recibo.append(String.format("Cliente:          %s
", nomeHigienizado));
        recibo.append(DIVISOR_RECIBO).append("
");
        
        // Cabeçalho da Tabela
        recibo.append(String.format(CABECALHO_RECIBO, "Produto", "Preço (R$)", "Qtd", "Total (R$)")).append("
");
        recibo.append(DIVISOR_RECIBO).append("
");

        // Loop de impressão dos Itens
        for (int i = 0; i < produtos.length; i++) {
            double totalItem = precos[i] * quantidades[i];
            String linhaItem = formatarLinhaDoRecibo(produtos[i], precos[i], quantidades[i], totalItem);
            recibo.append(linhaItem).append("
");
        }

        recibo.append(DIVISOR_RECIBO).append("
");
        recibo.append(String.format("SUBTOTAL:                                        R$ %10.2f
", subtotal));
        recibo.append(String.format("FRETE (+):                                       R$ %10.2f
", frete));
        recibo.append(String.format("DESCONTO (-):                                    R$ %10.2f
", desconto));
        recibo.append(DIVISOR_RECIBO).append("
");
        recibo.append(String.format("TOTAL DO PEDIDO:                                 R$ %10.2f
", total));
        recibo.append(DIVISOR_RECIBO).append("
");

        return recibo.toString();
    }

    // ── MÉTODOS AUXILIARES DE SUPORTE (ORGANIZAÇÃO DE CÓDIGO) ──────────────────────────

    /**
     * Valida se os vetores essenciais do carrinho de compras estão íntegros e consistentes.
     * 
     * @param precos Vetor com preços unitários.
     * @param quantidades Vetor com volumes comprados.
     */
    private static void validarVetoresItens(double[] precos, int[] quantidades) {
        if (precos == null || quantidades == null) {
            throw new IllegalArgumentException("Erro: Os vetores de preços ou quantidades não podem ser nulos.");
        }
        if (precos.length == 0 || quantidades.length == 0) {
            throw new IllegalArgumentException("Erro: O carrinho de compras não pode estar vazio.");
        }
        if (precos.length != quantidades.length) {
            throw new IllegalArgumentException("Erro: Divergência de dados: vetores de preços e quantidades têm tamanhos diferentes.");
        }
    }
}
