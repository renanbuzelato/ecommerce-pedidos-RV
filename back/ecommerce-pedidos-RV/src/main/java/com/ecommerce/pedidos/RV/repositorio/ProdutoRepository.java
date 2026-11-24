package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Substitui o ProdutoDAO (132 linhas de JDBC) + RepositorioProduto (12 linhas).
 * save, findById, findAll, deleteById e count vêm de graça pela herança.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String trecho);   // WHERE nome LIKE ?
    List<Produto> findByAtivoTrue();                               // WHERE ativo = TRUE
    Optional<Produto> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);   // edição: ignora o próprio produto
}
