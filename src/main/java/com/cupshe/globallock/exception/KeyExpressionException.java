package com.cupshe.globallock.exception;

/**
 * KeyExpressionException
 *
 * @author zxy
 */
public class KeyExpressionException extends RuntimeException {

    public KeyExpressionException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
