package com.petcare.model.exception;

/**
 * Custom exception class for Petcare application
 * Used throughout the application for business logic errors and data validation
 */
public class PetcareException extends Exception {

    /**
     * Constructs a new PetcareException with the specified detail message
     *
     * @param message the detail message
     */
    public PetcareException(String message) {
        super(message);
    }

    /**
     * Constructs a new PetcareException with the specified detail message and cause
     *
     * @param message the detail message
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public PetcareException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PetcareException with the specified cause
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public PetcareException(Throwable cause) {
        super(cause);
    }
}
