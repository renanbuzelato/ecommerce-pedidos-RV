package com.ecommerce.pedidos.RV.servico;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Primeiro bean "na mão" (Momento 2, Passo 5).
 * O @Component faz o container criar UMA instância desta classe e entregá-la
 * a quem pedir no construtor. Teste: tire o @Component e suba a aplicação -
 * o Spring diz qual bean faltou e quem precisava dele.
 */
@Component
public class CalculadoraFrete {

    private static final BigDecimal FRETE_GRATIS_A_PARTIR_DE = new BigDecimal("200");
    private static final BigDecimal FRETE_PADRAO = new BigDecimal("19.90");

    public BigDecimal calcular(BigDecimal valorPedido) {
        return valorPedido.compareTo(FRETE_GRATIS_A_PARTIR_DE) >= 0
                ? BigDecimal.ZERO
                : FRETE_PADRAO;
    }
}
