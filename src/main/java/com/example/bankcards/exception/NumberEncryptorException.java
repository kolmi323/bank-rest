package com.example.bankcards.exception;

public class NumberEncryptorException extends RuntimeException {
    public NumberEncryptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberEncryptorException(String message) {
        super(message);
    }
}
