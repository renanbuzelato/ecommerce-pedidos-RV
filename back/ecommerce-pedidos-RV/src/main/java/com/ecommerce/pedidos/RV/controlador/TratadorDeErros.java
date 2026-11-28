package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.CampoInvalido;
import com.ecommerce.pedidos.RV.controlador.dto.ErroResponse;
import com.ecommerce.pedidos.RV.excecao.ClienteDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ClienteNaoEncontradoException;
import com.ecommerce.pedidos.RV.excecao.ECommerceException;
import com.ecommerce.pedidos.RV.excecao.PagamentoNaoEncontradoException;
import com.ecommerce.pedidos.RV.excecao.PedidoNaoEncontradoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cada exceção no seu código HTTP (Aula 15) — a hierarquia da Aula 09 encontra aqui o seu destino.
 * Um único lugar para os erros da API inteira e nenhum try/catch nos controladores.
 *
 *   *NaoEncontradoException                                   -> 404 Não encontrado
 *   *DuplicadoException, estoque insuficiente, pedido inválido,
 *   regra de estado (IllegalStateException), integridade      -> 409 Conflito
 *   Bean Validation (@Valid)                                  -> 400 Dados inválidos (+ campos)
 *   IllegalArgumentException, JSON malformado, id não numérico -> 400 Requisição inválida
 *
 * As mensagens dizem o que aconteceu em linguagem de negócio, sem nome de tabela, classe ou SQL.
 */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler({ProdutoNaoEncontradoException.class, ClienteNaoEncontradoException.class,
            PedidoNaoEncontradoException.class, PagamentoNaoEncontradoException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)                     // 404
    public ErroResponse naoEncontrado(RuntimeException e, HttpServletRequest req) {
        return new ErroResponse(404, "Não encontrado", e.getMessage(), req.getRequestURI());
    }

    // EstoqueInsuficiente, PedidoInvalido e PagamentoRecusado herdam de ECommerceException (Aula 09)
    @ExceptionHandler({ECommerceException.class, ProdutoDuplicadoException.class,
            ClienteDuplicadoException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)                      // 409
    public ErroResponse conflito(Exception e, HttpServletRequest req) {
        return new ErroResponse(409, "Conflito", e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)                      // 409
    public ErroResponse integridade(DataIntegrityViolationException e, HttpServletRequest req) {
        return new ErroResponse(409, "Conflito",
                "O registro está em uso ou já existe.", req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)                   // 400
    public ErroResponse dadosInvalidos(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<CampoInvalido> campos = e.getFieldErrors().stream()
                .map(CampoInvalido::new).toList();
        // a mensagem principal já traz o primeiro problema, para o front exibir direto
        String mensagem = campos.size() == 1 ? campos.get(0).mensagem() : "Há campos inválidos na requisição";
        return new ErroResponse(400, "Dados inválidos", mensagem, req.getRequestURI(),
                LocalDateTime.now(), campos);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)                   // 400
    public ErroResponse argumentoInvalido(IllegalArgumentException e, HttpServletRequest req) {
        return new ErroResponse(400, "Requisição inválida", e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)                   // 400
    public ErroResponse corpoInvalido(Exception e, HttpServletRequest req) {
        return new ErroResponse(400, "Requisição inválida",
                "Requisição com formato inválido.", req.getRequestURI());
    }
}
