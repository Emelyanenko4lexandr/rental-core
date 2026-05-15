package dev.rentalcore.entity.enums;

/**
 * Статус записи об аренде.
 *
 * <ul>
 *   <li>{@link #ACTIVE} — аренда активна, объект используется арендатором</li>
 *   <li>{@link #FINISHED} — аренда завершена</li>
 * </ul>
 */
public enum RentStatus {

    /** Аренда активна. */
    ACTIVE,

    /** Аренда завершена. */
    FINISHED
}
