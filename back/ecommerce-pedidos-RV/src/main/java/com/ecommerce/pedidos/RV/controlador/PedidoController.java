package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.PedidoEntrada;
import com.ecommerce.pedidos.RV.controlador.dto.PedidoResposta;
import com.ecommerce.pedidos.RV.controlador.dto.SituacaoEntrada;
import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.servico.ServicoPedido;
import com.ecommerce.pedidos.RV.servico.ServicoPedido.ItemSolicitado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 *   GET   /api/pedidos        200 lista (mais novos primeiro)
 *   GET   /api/pedidos/{id}   200 | 404
 *   POST  /api/pedidos        201 + Location | 400 | 404 | 409 (estoque insuficiente, produto inativo)
 *   PATCH /api/pedidos/{id}   200 | 404 | 409 (transição inválida)
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final ServicoPedido servico;
    private final PedidoMapper mapper;

    public PedidoController(ServicoPedido servico, PedidoMapper mapper) {
        this.servico = servico;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PedidoResposta> listar() {
        return servico.listarTodos().stream().map(mapper::paraResposta).toList();
    }

    @GetMapping("/{id}")
    public PedidoResposta buscar(@PathVariable Long id) {
        return mapper.paraResposta(servico.buscarComItens(id));
    }

    @PostMapping
    public ResponseEntity<PedidoResposta> criar(@Valid @RequestBody PedidoEntrada entrada,
                                                UriComponentsBuilder uriBuilder)
            throws EstoqueInsuficienteException, PedidoInvalidoException {
        List<ItemSolicitado> itens = entrada.itens().stream()
                .map(i -> new ItemSolicitado(i.produtoId(), i.quantidade()))
                .toList();
        Pedido pedido = servico.criar(entrada.clienteId(), entrada.enderecoEntrega().paraEntrega(), itens);
        return ResponseEntity.created(uriBuilder.path("/api/pedidos/{id}").buildAndExpand(pedido.getId()).toUri())
                .body(mapper.paraResposta(pedido));
    }

    @PatchMapping("/{id}")
    public PedidoResposta alterarSituacao(@PathVariable Long id, @Valid @RequestBody SituacaoEntrada entrada)
            throws PedidoInvalidoException {
        return mapper.paraResposta(servico.alterarSituacao(id, entrada.situacao()));
    }
}
