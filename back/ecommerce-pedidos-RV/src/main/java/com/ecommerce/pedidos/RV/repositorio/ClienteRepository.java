package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByNomeContainingIgnoreCase(String trecho);
    boolean existsByEmail(String email);          // SELECT COUNT(*) ... WHERE email = ?
    boolean existsByDocumento(String cpf);        // o atributo Java é "documento" (coluna cpf)
}
