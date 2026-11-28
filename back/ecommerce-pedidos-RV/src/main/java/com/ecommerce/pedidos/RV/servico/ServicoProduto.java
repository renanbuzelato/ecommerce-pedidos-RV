package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.ProdutoDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoNaoEncontradoException;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.repositorio.PedidoRepository;
import com.ecommerce.pedidos.RV.repositorio.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regra de negócio de produto. Não conhece SQL nem HTTP.
 * É a fronteira transacional: o @Transactional mora aqui, não no repositório.
 */
@Service
public class ServicoProduto {

    private final ProdutoRepository repositorio;
    private final PedidoRepository pedidos;

    // Construtor único: o Spring injeta sozinho, sem @Autowired e sem "new"
    public ServicoProduto(ProdutoRepository repositorio, PedidoRepository pedidos) {
        this.repositorio = repositorio;
        this.pedidos = pedidos;
    }

    @Transactional
    public Produto cadastrar(Produto produto) {
        if (repositorio.existsByNome(produto.getNome())) {
            throw new ProdutoDuplicadoException(produto.getNome());
        }
        return repositorio.save(produto);
    }

    @Transactional(readOnly = true)
    public Produto buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarAtivos() {
        return repositorio.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorNome(String trecho) {
        return repositorio.findByNomeContainingIgnoreCase(trecho);
    }

    /**
     * Sem chamar save(): dentro da transação o produto está "gerenciado" e o
     * Hibernate grava o UPDATE sozinho no commit (dirty checking).
     */
    @Transactional
    public Produto alterarPreco(Long id, BigDecimal novoPreco) {
        Produto produto = buscar(id);
        produto.alterarPreco(novoPreco);
        return produto;
    }

    /** Edição completa feita pela área administrativa (PUT /api/produtos/{id}). */
    @Transactional
    public Produto atualizar(Long id, String nome, String descricao, String categoria,
                             BigDecimal preco, int estoque, boolean ativo) {
        Produto produto = buscar(id);
        if (repositorio.existsByNomeAndIdNot(nome.trim(), id)) {
            throw new ProdutoDuplicadoException(nome);
        }
        produto.alterarDados(nome, preco, estoque);
        produto.setDescricao(descricao);
        produto.setCategoria(categoria);
        if (ativo) produto.ativar(); else produto.desativar();
        return produto;                       // dirty checking grava o UPDATE no commit
    }

    @Transactional
    public Produto reporEstoque(Long id, int quantidade) {
        Produto produto = buscar(id);
        produto.reporEstoque(quantidade);
        return produto;
    }

    /** Exclusão lógica: produto que já foi vendido não pode sumir do banco (RESTRICT). */
    @Transactional
    public void desativar(Long id) {
        buscar(id).desativar();
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscar(id);
        // Política RESTRICT (Aula 13) checada na regra, com mensagem clara em vez de erro de FK
        if (pedidos.existsByItens_Produto_Id(id)) {
            throw new IllegalStateException("\"" + produto.getNome()
                    + "\" já aparece em pedidos e não pode ser excluído. Desative-o na edição.");
        }
        repositorio.delete(produto);
    }
}
