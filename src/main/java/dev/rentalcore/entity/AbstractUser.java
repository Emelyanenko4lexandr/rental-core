package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.BaseRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Абстрактный базовый класс пользователя системы аренды.
 *
 * <p>Содержит универсальный набор полей, необходимых любому пользователю
 * независимо от предметной области. Клиент-разработчик наследует этот класс
 * и добавляет специфичные для своего проекта поля.</p>
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "users")
 * public class User extends AbstractUser {
 *
 *     private String passportNumber;  // специфика сервиса аренды авто
 *     private String driverLicense;
 * }
 * }</pre>
 *
 * @see BaseRole
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractUser {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Полное имя пользователя (ФИО или отображаемое имя).
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Роль пользователя в системе.
     * По умолчанию используется {@link BaseRole}.
     * Клиент может хранить здесь строку и маппить на собственный enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseRole role;

    /**
     * Баланс пользователя в системе.
     * Используется для расчётов стоимости аренды.
     */
    @Column(nullable = false)
    private Double balance = 0.0;

    /**
     * Флаг активности учётной записи.
     * {@code false} означает, что аккаунт заблокирован.
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Конструктор для создания пользователя с основными параметрами.
     *
     * @param fullName полное имя пользователя
     * @param role     роль пользователя
     * @param balance  начальный баланс
     * @param enabled  активен ли аккаунт
     */
    public AbstractUser(String fullName, BaseRole role, Double balance, boolean enabled) {
        this.fullName = fullName;
        this.role = role;
        this.balance = balance;
        this.enabled = enabled;
    }
}
