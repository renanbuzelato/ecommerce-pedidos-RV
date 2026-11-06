package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.modelo.Produto;
import java.util.List;
import java.util.Optional;

public interface RepositorioProduto {
    Produto salvar(Produto produto);
    void atualizar(Produto produto);
    void excluir(Long id);
    Optional<Produto> buscarPorId(Long id);
    List<Produto> listarTodos();
}