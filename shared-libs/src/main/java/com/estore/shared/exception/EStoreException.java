package com.estore.shared.exception;

/**
 * Base exception class for all E-Store application exceptions.
 */
public class EStoreException extends RuntimeException {

    public EStoreException(String message) {
        super(message);
    }

    public EStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
