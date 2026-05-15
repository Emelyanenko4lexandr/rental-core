package dev.rentalcore.exception;

/**
 * Исключение, выбрасываемое когда запрошенный ресурс не найден.
 * Соответствует HTTP 404 Not Found.
 */
public class RentalNotFoundException extends RuntimeException {

    public RentalNotFoundException(String message) {
        super(message);
    }

    public RentalNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }
}
