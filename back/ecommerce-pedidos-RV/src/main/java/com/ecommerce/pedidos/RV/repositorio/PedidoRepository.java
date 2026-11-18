package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);               // WHERE cliente_id = ?
    List<Pedido> findBySituacao(SituacaoPedido situacao);       // WHERE situacao = ?
    boolean existsByClienteId(Long clienteId);

    /**
     * Consulta com JOIN: nome de método ficaria ilegível, então JPQL explícito.
     * O JOIN FETCH traz pedido + itens + produtos num SELECT só (evita o N+1).
     */
    @Query("""
           SELECT DISTINCT p FROM Pedido p
           JOIN FETCH p.cliente
           LEFT JOIN FETCH p.itens i
           LEFT JOIN FETCH i.produto
           WHERE p.id = :id
           """)
    Optional<Pedido> buscarComItens(@Param("id") Long id);
}
