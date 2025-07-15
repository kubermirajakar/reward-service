package com.kubertech.rewardsystem.exception;

/**
 * Custom exception thrown when a requested resource is not found in the system.
 * <p>
 * Typically used for entities such as {@code Customer} or {@code Transaction}
 * when queried by ID and the corresponding record is missing from the database.
 * <p>
 * This exception results in a 404 HTTP response when handled by the global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with a specified error message.
     *
     * @param message descriptive message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}