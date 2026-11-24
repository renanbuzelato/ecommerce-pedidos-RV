package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.ClienteEntrada;
import com.ecommerce.pedidos.RV.controlador.dto.ClienteResposta;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.servico.ServicoCliente;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 *   GET  /api/clientes        200 lista
 *   GET  /api/clientes/{id}   200 | 404
 *   POST /api/clientes        201 | 400 | 409 (e-mail/CPF repetido)
 *   PUT  /api/clientes/{id}   200 | 400 | 404 | 409
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ServicoCliente servico;

    public ClienteController(ServicoCliente servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<ClienteResposta> listar() {
        return servico.listarTodos().stream().map(ClienteResposta::de).toList();
    }

    @GetMapping("/{id}")
    public ClienteResposta buscar(@PathVariable Long id) {
        return ClienteResposta.de(servico.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResposta> cadastrar(@Valid @RequestBody ClienteEntrada entrada) {
        Cliente cliente = new Cliente(entrada.nome(), entrada.documento(), entrada.email());
        cliente.setTelefone(entrada.telefone());
        entrada.enderecosComoEntidades().forEach(cliente::adicionarEndereco);

        Cliente salvo = servico.cadastrar(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + salvo.getId()))
                .body(ClienteResposta.de(salvo));
    }

    @PutMapping("/{id}")
    public ClienteResposta atualizar(@PathVariable Long id, @Valid @RequestBody ClienteEntrada entrada) {
        return ClienteResposta.de(servico.atualizar(id, entrada.nome(), entrada.documento(), entrada.email(),
                entrada.telefone(), entrada.enderecosComoEntidades()));
    }
}
