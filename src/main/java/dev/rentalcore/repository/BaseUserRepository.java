package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Базовый репозиторий для работы с пользователями.
 *
 * <p>Расширяет {@link JpaRepository}, добавляя методы, универсально
 * применимые к любой системе аренды. Клиент расширяет этот интерфейс
 * и добавляет специфичные для проекта методы поиска.</p>
 *
 * <p>Аннотация {@code @NoRepositoryBean} указывает Spring Data,
 * что этот интерфейс не нужно инстанциировать напрямую — он
 * является лишь базовым для дочерних репозиториев клиента.</p>
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * public interface UserRepository extends BaseUserRepository<User> {
 *
 *     // Поиск по username из связанной сущности Credentials
 *     Optional<User> findByCredentialsUsername(String username);
 *
 *     // Поиск по номеру телефона
 *     Optional<User> findByPhone(String phone);
 * }
 * }</pre>
 *
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 */
@NoRepositoryBean
public interface BaseUserRepository<U extends AbstractUser> extends JpaRepository<U, Long> {

    /**
     * Найти всех активных пользователей.
     *
     * @return список пользователей у которых {@code enabled = true}
     */
    List<U> findByEnabledTrue();

    /**
     * Найти всех пользователей с указанной ролью.
     *
     * @param role строковое представление роли
     * @return список пользователей с указанной ролью
     */
    List<U> findByRole(String role);
}
