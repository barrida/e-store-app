package com.estore.shared.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends EStoreException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s not found with identifier: %s", resourceName, identifier));
    }
}
