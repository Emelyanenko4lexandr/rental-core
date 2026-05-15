package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractMessage;
import dev.rentalcore.entity.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Базовый репозиторий для работы с системными сообщениями.
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * public interface MessageRepository
 *         extends BaseMessageRepository<Message, User> {
 *
 *     // Непрочитанные сообщения
 *     List<Message> findByRecipientAndReadFalse(User recipient);
 * }
 * }</pre>
 *
 * @param <M> тип сообщения, должен расширять {@link AbstractMessage}
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 */
@NoRepositoryBean
public interface BaseMessageRepository<M extends AbstractMessage, U extends AbstractUser>
        extends JpaRepository<M, Long> {

    /**
     * Найти все сообщения для указанного получателя.
     *
     * @param recipient получатель сообщений
     * @return список сообщений
     */
    List<M> findByRecipient(U recipient);

    /**
     * Найти все сообщения, отправленные указанным пользователем.
     *
     * @param sender отправитель
     * @return список отправленных сообщений
     */
    List<M> findBySender(U sender);
}
