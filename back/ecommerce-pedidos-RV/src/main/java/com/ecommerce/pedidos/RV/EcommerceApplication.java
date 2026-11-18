package com.ecommerce.pedidos.RV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação (substitui o antigo App.java com main manual).
 *
 * @SpringBootApplication liga a varredura de componentes a partir DESTE pacote:
 * tudo que estiver em com.ecommerce.pedidos.RV.* e tiver @Component, @Service,
 * @Repository ou @Entity é encontrado automaticamente pelo container.
 */
@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
