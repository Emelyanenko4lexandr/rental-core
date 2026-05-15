package dev.rentalcore.exception;

/**
 * Исключение, выбрасываемое при некорректных входных данных.
 * Соответствует HTTP 400 Bad Request.
 */
public class RentalBadRequestException extends RuntimeException {

    public RentalBadRequestException(String message) {
        super(message);
    }
}
