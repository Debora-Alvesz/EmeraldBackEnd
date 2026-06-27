package com.emerald.infrastructure.exception;

/**
 * Exceção para regras de negócio violadas.
 * Exemplos: nome duplicado, dados inválidos, etc.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}