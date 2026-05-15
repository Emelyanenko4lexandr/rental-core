package dev.rentalcore.entity.enums;

/**
 * Тип системного сообщения.
 *
 * <ul>
 *   <li>{@link #APPROVED} — действие одобрено</li>
 *   <li>{@link #DENIED} — действие отклонено</li>
 * </ul>
 */
public enum MessageType {

    /** Действие одобрено (например, объект прошёл верификацию). */
    APPROVED,

    /** Действие отклонено (например, объект не прошёл верификацию). */
    DENIED
}
