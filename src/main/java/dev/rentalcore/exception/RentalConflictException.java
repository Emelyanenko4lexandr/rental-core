package dev.rentalcore.exception;

/**
 * Исключение, выбрасываемое при конфликте данных.
 * Например, при попытке создать дублирующую запись.
 * Соответствует HTTP 409 Conflict.
 */
public class RentalConflictException extends RuntimeException {

    public RentalConflictException(String message) {
        super(message);
    }
}
