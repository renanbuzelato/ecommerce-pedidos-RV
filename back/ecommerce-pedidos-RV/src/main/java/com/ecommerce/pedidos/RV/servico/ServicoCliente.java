package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.ClienteDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ClienteNaoEncontradoException;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Endereco;
import com.ecommerce.pedidos.RV.repositorio.ClienteRepository;
import com.ecommerce.pedidos.RV.repositorio.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoCliente {

    private final ClienteRepository repositorio;
    private final PedidoRepository pedidos;

    public ServicoCliente(ClienteRepository repositorio, PedidoRepository pedidos) {
        this.repositorio = repositorio;
        this.pedidos = pedidos;
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        if (repositorio.existsByEmail(cliente.getEmail())) {
            throw new ClienteDuplicadoException("e-mail", cliente.getEmail());
        }
        if (repositorio.existsByDocumento(cliente.getDocumento())) {
            throw new ClienteDuplicadoException("CPF", cliente.getDocumento());
        }
        return repositorio.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String trecho) {
        return repositorio.findByNomeContainingIgnoreCase(trecho);
    }

    @Transactional
    public Cliente alterarEmail(Long id, String novoEmail) {
        Cliente cliente = buscar(id);
        if (!novoEmail.equalsIgnoreCase(cliente.getEmail()) && repositorio.existsByEmail(novoEmail.toLowerCase())) {
            throw new ClienteDuplicadoException("e-mail", novoEmail);
        }
        cliente.setEmail(novoEmail);
        return cliente;
    }

    /** Edição completa (PUT /api/clientes/{id}): dados pessoais e lista de endereços. */
    @Transactional
    public Cliente atualizar(Long id, String nome, String cpf, String email, String telefone,
                             List<Endereco> enderecos) {
        Cliente cliente = buscar(id);
        String emailNormalizado = email.trim().toLowerCase();
        if (repositorio.existsByEmailAndIdNot(emailNormalizado, id)) {
            throw new ClienteDuplicadoException("e-mail", email);
        }
        if (repositorio.existsByDocumentoAndIdNot(cpf, id)) {
            throw new ClienteDuplicadoException("CPF", cpf);
        }
        cliente.setNome(nome);
        cliente.setDocumento(cpf);
        cliente.setEmail(emailNormalizado);
        cliente.setTelefone(telefone);
        cliente.substituirEnderecos(enderecos);
        return cliente;
    }

    /** Política RESTRICT da Aula 13, agora checada na regra antes de bater no banco. */
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        if (pedidos.existsByClienteId(id)) {
            throw new IllegalStateException("Cliente " + id + " possui pedidos e não pode ser excluído.");
        }
        repositorio.delete(cliente);
    }
}
