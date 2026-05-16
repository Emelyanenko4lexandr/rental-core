package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractMessage;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.MessageType;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseMessageRepository;
import dev.rentalcore.repository.BaseUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractMessageService<M extends AbstractMessage, U extends AbstractUser> {

    protected final BaseMessageRepository<M, U> messageRepository;
    protected final BaseUserRepository<U> userRepository;

    protected AbstractMessageService(
            BaseMessageRepository<M, U> messageRepository,
            BaseUserRepository<U> userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<M> getMessages(Long recipientId) {
        U recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RentalNotFoundException("User", recipientId));
        return messageRepository.findByRecipient(recipient);
    }

    @Transactional
    public M sendMessage(Long senderId, Long recipientId, String text, MessageType type) {
        U sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RentalNotFoundException("User", senderId));
        U recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RentalNotFoundException("User", recipientId));

        M message = createMessageInstance(sender, recipient, text, type);
        return messageRepository.save(message);
    }


    @Transactional
    public void deleteAllMessages(Long recipientId) {
        U recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RentalNotFoundException("User", recipientId));
        List<M> messages = messageRepository.findByRecipient(recipient);
        messageRepository.deleteAll(messages);
    }

    protected abstract M createMessageInstance(U sender, U recipient, String text, MessageType type);
}
