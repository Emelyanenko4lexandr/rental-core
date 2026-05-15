package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.exception.RentalBadRequestException;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Абстрактный сервис для работы с пользователями.
 *
 * <p>Содержит готовую реализацию базовых операций, которые одинаковы
 * для любой системы аренды. Клиент наследует этот класс, передаёт
 * в конструктор свой репозиторий и при необходимости переопределяет
 * методы или добавляет новые.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Service
 * public class UserService extends AbstractUserService<User> {
 *
 *     public UserService(UserRepository userRepository) {
 *         super(userRepository);
 *     }
 *
 *     // Метод обязателен к реализации
 *     @Override
 *     public User register(String fullName, String password) {
 *         // специфичная логика регистрации
 *     }
 *
 *     // Опциональный дополнительный метод
 *     public User findByUsername(String username) {
 *         return userRepository.findByCredentialsUsername(username)
 *                 .orElseThrow(() -> new RentalNotFoundException("User", username));
 *     }
 * }
 * }</pre>
 *
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 */
public abstract class AbstractUserService<U extends AbstractUser> {

    protected final BaseUserRepository<U> userRepository;

    protected AbstractUserService(BaseUserRepository<U> userRepository) {
        this.userRepository = userRepository;
    }

    // -----------------------------------------------------------------------
    // Готовые методы
    // -----------------------------------------------------------------------

    /**
     * Найти пользователя по идентификатору.
     * Бросает {@link RentalNotFoundException} если пользователь не найден.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws RentalNotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public U getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException("User", id));
    }

    /**
     * Получить список всех активных пользователей.
     *
     * @return список пользователей с {@code enabled = true}
     */
    @Transactional(readOnly = true)
    public List<U> getAllActive() {
        return userRepository.findByEnabledTrue();
    }

    /**
     * Пополнить баланс пользователя.
     *
     * @param id  идентификатор пользователя
     * @param sum сумма пополнения (должна быть больше нуля)
     * @return новый баланс пользователя
     * @throws RentalNotFoundException   если пользователь не найден
     * @throws RentalBadRequestException если сумма <= 0
     */
    @Transactional
    public Double addBalance(Long id, Double sum) {
        if (sum == null || sum <= 0) {
            throw new RentalBadRequestException("Amount must be greater than zero");
        }
        U user = getById(id);
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
        return user.getBalance();
    }

    /**
     * Заблокировать пользователя (установить {@code enabled = false}).
     *
     * @param id идентификатор пользователя
     * @throws RentalNotFoundException если пользователь не найден
     */
    @Transactional
    public void disableUser(Long id) {
        U user = getById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Разблокировать пользователя (установить {@code enabled = true}).
     *
     * @param id идентификатор пользователя
     * @throws RentalNotFoundException если пользователь не найден
     */
    @Transactional
    public void enableUser(Long id) {
        U user = getById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    // -----------------------------------------------------------------------
    // Абстрактные методы — обязательны к реализации клиентом
    // -----------------------------------------------------------------------

    /**
     * Зарегистрировать нового пользователя.
     *
     * <p>Метод намеренно оставлен абстрактным: логика регистрации
     * (набор обязательных полей, валидация, хэширование пароля, отправка
     * письма подтверждения) в каждом проекте своя.</p>
     *
     * @param fullName полное имя пользователя
     * @param password пароль в открытом виде
     * @return созданный пользователь
     */
    public abstract U register(String fullName, String password);
}
