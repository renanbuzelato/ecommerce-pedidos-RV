package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.ErroResposta;
import com.ecommerce.pedidos.RV.excecao.ClienteDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ClienteNaoEncontradoException;
import com.ecommerce.pedidos.RV.excecao.ECommerceException;
import com.ecommerce.pedidos.RV.excecao.PedidoNaoEncontradoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoDuplicadoException;
import com.ecommerce.pedidos.RV.excecao.ProdutoNaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduz as exceções do domínio (Aula 09) e do serviço (Aula 14) em códigos HTTP.
 * Nenhum stack trace chega ao cliente: só { status, mensagem, timestamp }.
 *
 *   *NaoEncontradoException                         -> 404
 *   *DuplicadoException, violação de integridade    -> 409
 *   ECommerceException (estoque, pedido inválido),
 *   IllegalStateException (regra de estado)         -> 422
 *   IllegalArgumentException, Bean Validation, JSON -> 400
 */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler({ProdutoNaoEncontradoException.class, ClienteNaoEncontradoException.class,
            PedidoNaoEncontradoException.class})
    public ResponseEntity<ErroResposta> naoEncontrado(RuntimeException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ProdutoDuplicadoException.class, ClienteDuplicadoException.class})
    public ResponseEntity<ErroResposta> duplicado(RuntimeException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException e) {
        return resposta(HttpStatus.CONFLICT, "Operação viola uma restrição do banco (registro em uso ou duplicado).");
    }

    @ExceptionHandler({ECommerceException.class, IllegalStateException.class})
    public ResponseEntity<ErroResposta> regraDeNegocio(Exception e) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> argumentoInvalido(IllegalArgumentException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos.");
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> corpoInvalido(Exception e) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição com formato inválido.");
    }

    private ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(ErroResposta.de(status.value(), mensagem));
    }
}
