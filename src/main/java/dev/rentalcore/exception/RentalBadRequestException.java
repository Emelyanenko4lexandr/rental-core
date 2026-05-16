package dev.rentalcore.exception;

public class RentalBadRequestException extends RuntimeException {

    public RentalBadRequestException(String message) {
        super(message);
    }
}
