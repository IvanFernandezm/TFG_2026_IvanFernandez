package org.example.tribunalsbackend.Config.Exceptions;

public class ConstraintFailureException extends RuntimeException {
    public ConstraintFailureException(String message) {
        super(message);
    }
}
