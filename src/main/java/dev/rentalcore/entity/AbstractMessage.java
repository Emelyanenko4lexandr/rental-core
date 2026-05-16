package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractMessage<U extends AbstractUser> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private U sender;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private U recipient;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageText;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    private String reason;

    @Column(nullable = false)
    private Instant sentAt;

    public AbstractMessage(U sender, U recipient, String messageText, MessageType type) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.type = type;
        this.sentAt = Instant.now();
    }

    public AbstractMessage(U sender, U recipient, String messageText, MessageType type, String reason) {
        this(sender, recipient, messageText, type);
        this.reason = reason;
    }
}
