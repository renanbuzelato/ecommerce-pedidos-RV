package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.ProdutoDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoNaoEncontradoException;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.repositorio.PedidoRepository;
import com.ecommerce.pedidos.RV.repositorio.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste unitário do serviço com Mockito (Aula 11): o repositório é um dublê,
 * então não precisa de Spring nem de banco.
 */
@ExtendWith(MockitoExtension.class)
class ServicoProdutoTest {

    @Mock
    private ProdutoRepository repositorio;

    @Mock
    private PedidoRepository pedidos;

    @InjectMocks
    private ServicoProduto servico;

    @Test
    @DisplayName("Deve salvar produto com nome inédito")
    void deveCadastrarProdutoNovo() {
        Produto mouse = new Produto("Mouse", new BigDecimal("50.00"), 10);
        when(repositorio.existsByNome("Mouse")).thenReturn(false);
        when(repositorio.save(mouse)).thenReturn(mouse);

        assertSame(mouse, servico.cadastrar(mouse));
        verify(repositorio).save(mouse);
    }

    @Test
    @DisplayName("Não deve salvar produto com nome repetido")
    void naoDeveCadastrarDuplicado() {
        Produto mouse = new Produto("Mouse", new BigDecimal("50.00"), 10);
        when(repositorio.existsByNome("Mouse")).thenReturn(true);

        assertThrows(ProdutoDuplicadoException.class, () -> servico.cadastrar(mouse));
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException para id inexistente")
    void deveLancarNaoEncontrado() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> servico.buscar(99L));
    }
}
