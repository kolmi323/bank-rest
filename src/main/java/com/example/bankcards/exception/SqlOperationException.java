package com.example.bankcards.exception;

public class SqlOperationException extends RuntimeException {
    public SqlOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlOperationException(String message) {
        super(message);
    }
}
