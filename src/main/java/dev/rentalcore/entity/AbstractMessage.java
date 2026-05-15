package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

/**
 * Абстрактный базовый класс системного сообщения.
 *
 * <p>Используется для уведомлений между пользователями системы:
 * результаты верификации объектов, статусы сделок, сообщения поддержки.</p>
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "messages")
 * public class Message extends AbstractMessage<User> {
 *
 *     // Дополнительное поле, специфичное для проекта
 *     private Long relatedRentId;
 * }
 * }</pre>
 *
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 * @see MessageType
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractMessage<U extends AbstractUser> {

    /**
     * Уникальный идентификатор сообщения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Отправитель сообщения.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private U sender;

    /**
     * Получатель сообщения.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private U recipient;

    /**
     * Текст сообщения.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageText;

    /**
     * Тип сообщения (одобрено / отклонено).
     *
     * @see MessageType
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    /**
     * Дополнительная причина или комментарий.
     * Используется, например, при отклонении верификации.
     */
    private String reason;

    /**
     * Время отправки сообщения. Устанавливается автоматически.
     */
    @Column(nullable = false)
    private Instant sentAt;

    /**
     * Конструктор для создания сообщения.
     *
     * @param sender      отправитель
     * @param recipient   получатель
     * @param messageText текст сообщения
     * @param type        тип сообщения
     */
    public AbstractMessage(U sender, U recipient, String messageText, MessageType type) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.type = type;
        this.sentAt = Instant.now();
    }

    /**
     * Конструктор для создания сообщения с причиной.
     *
     * @param sender      отправитель
     * @param recipient   получатель
     * @param messageText текст сообщения
     * @param type        тип сообщения
     * @param reason      причина или комментарий
     */
    public AbstractMessage(U sender, U recipient, String messageText, MessageType type, String reason) {
        this(sender, recipient, messageText, type);
        this.reason = reason;
    }
}
