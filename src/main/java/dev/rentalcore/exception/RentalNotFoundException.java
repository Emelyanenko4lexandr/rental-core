package dev.rentalcore.exception;

public class RentalNotFoundException extends RuntimeException {

    public RentalNotFoundException(String message) {
        super(message);
    }

    public RentalNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }
}
