package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.ProdutoEntrada;
import com.ecommerce.pedidos.RV.controlador.dto.ProdutoResposta;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.servico.ServicoProduto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Camada HTTP de produtos. Só traduz HTTP <-> serviço: nenhuma regra de negócio aqui.
 *   GET    /api/produtos        200 lista
 *   GET    /api/produtos/{id}   200 | 404
 *   POST   /api/produtos        201 + Location | 400 | 409
 *   PUT    /api/produtos/{id}   200 | 400 | 404 | 409
 *   DELETE /api/produtos/{id}   204 | 404 | 409 (já vendido: RESTRICT)
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ServicoProduto servico;

    public ProdutoController(ServicoProduto servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<ProdutoResposta> listar() {
        return servico.listarTodos().stream().map(ProdutoResposta::de).toList();
    }

    @GetMapping("/{id}")
    public ProdutoResposta buscar(@PathVariable Long id) {
        return ProdutoResposta.de(servico.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ProdutoResposta> cadastrar(@Valid @RequestBody ProdutoEntrada entrada,
                                                     UriComponentsBuilder uriBuilder) {
        Produto produto = new Produto(entrada.nome(), entrada.preco(), entrada.quantidadeEmEstoque());
        produto.setDescricao(entrada.descricao());
        produto.setCategoria(entrada.categoria());
        if (!entrada.ativoOuPadrao()) produto.desativar();

        Produto salvo = servico.cadastrar(produto);
        return ResponseEntity.created(uriBuilder.path("/api/produtos/{id}").buildAndExpand(salvo.getId()).toUri())
                .body(ProdutoResposta.de(salvo));
    }

    @PutMapping("/{id}")
    public ProdutoResposta atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoEntrada entrada) {
        return ProdutoResposta.de(servico.atualizar(id, entrada.nome(), entrada.descricao(), entrada.categoria(),
                entrada.preco(), entrada.quantidadeEmEstoque(), entrada.ativoOuPadrao()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)                    // 204, sem corpo
    public void excluir(@PathVariable Long id) {
        servico.excluir(id);
    }
}
