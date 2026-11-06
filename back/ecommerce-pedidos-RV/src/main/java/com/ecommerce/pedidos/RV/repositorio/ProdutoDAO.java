package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.excecao.RepositorioException;
import com.ecommerce.pedidos.RV.modelo.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAO implements RepositorioProduto {

    private static final String INSERIR = "INSERT INTO produto (nome, preco, quantidade) VALUES (?, ?, ?)";
    private static final String ATUALIZAR = "UPDATE produto SET nome = ?, preco = ?, quantidade = ? WHERE id = ?";
    private static final String EXCLUIR = "DELETE FROM produto WHERE id = ?";
    private static final String BUSCAR_POR_ID = "SELECT id, nome, preco, quantidade FROM produto WHERE id = ?";
    private static final String LISTAR_TODOS = "SELECT id, nome, preco, quantidade FROM produto";

    @Override
    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            return inserir(produto);
        } else {
            atualizar(produto);
            return produto;
        }
    }

    private Produto inserir(Produto produto) {
        try (Connection c = ConexaoFactory.abrir();
             PreparedStatement ps = c.prepareStatement(INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getQuantidadeEmEstoque());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                }
            }
            return produto;
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao inserir produto", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        if (produto.getId() == null) {
            throw new IllegalArgumentException("Produto sem ID não pode ser atualizado.");
        }

        try (Connection c = ConexaoFactory.abrir();
             PreparedStatement ps = c.prepareStatement(ATUALIZAR)) {

            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getQuantidadeEmEstoque());
            ps.setLong(4, produto.getId());

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new RepositorioException("Produto " + produto.getId() + " não encontrado.");
            }
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao atualizar produto", e);
        }
    }

    @Override
    public void excluir(Long id) {
        try (Connection c = ConexaoFactory.abrir();
             PreparedStatement ps = c.prepareStatement(EXCLUIR)) {

            ps.setLong(1, id);

            if (ps.executeUpdate() == 0) {
                throw new RepositorioException("Produto " + id + " não existe.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RepositorioException("O produto " + id + " está em pedidos e não pode ser excluído.", e);
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao excluir produto", e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        try (Connection c = ConexaoFactory.abrir();
             PreparedStatement ps = c.prepareStatement(BUSCAR_POR_ID)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto(
                        rs.getString("nome"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("quantidade")
                    );
                    p.setId(rs.getLong("id"));
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao buscar produto por ID", e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Connection c = ConexaoFactory.abrir();
             PreparedStatement ps = c.prepareStatement(LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto(
                    rs.getString("nome"),
                    rs.getBigDecimal("preco"),
                    rs.getInt("quantidade")
                );
                p.setId(rs.getLong("id"));
                lista.add(p);
            }
            return lista;
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao listar produtos", e);
        }
    }
}