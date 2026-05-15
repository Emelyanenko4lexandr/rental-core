package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.RentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

/**
 * Абстрактный базовый класс записи об аренде (сделки).
 *
 * <p>Фиксирует факт аренды: кто арендует, что арендует, когда началась
 * и завершилась аренда, текущий статус. Является центральной сущностью
 * библиотеки, связывающей арендатора и объект аренды.</p>
 *
 * <p>Класс параметризован двумя типами, что обеспечивает строгую
 * типизацию для конкретного проекта клиента.</p>
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "rents")
 * public class Rent extends AbstractRent<Automobile, User> {
 *
 *     // Можно добавить специфичные поля
 *     private String pickupAddress;
 *     private String returnAddress;
 * }
 * }</pre>
 *
 * @param <P> тип объекта аренды, должен расширять {@link AbstractRentalProduct}
 * @param <U> тип пользователя-арендатора, должен расширять {@link AbstractUser}
 * @see RentStatus
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractRent<P extends AbstractRentalProduct, U extends AbstractUser> {

    /**
     * Уникальный идентификатор записи об аренде.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Арендатор — пользователь, взявший объект в аренду.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private U tenant;

    /**
     * Арендуемый объект.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private P product;

    /**
     * Момент начала аренды. Устанавливается автоматически при создании записи.
     */
    @Column(nullable = false)
    private Instant startRental;

    /**
     * Момент завершения аренды.
     * {@code null} пока аренда активна.
     */
    private Instant endRental;

    /**
     * Текущий статус аренды.
     *
     * @see RentStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentStatus status;

    /**
     * Итоговая стоимость аренды.
     * Рассчитывается и устанавливается при завершении.
     * Может быть {@code null} пока аренда активна.
     */
    private Double totalCost;

    /**
     * Конструктор для создания новой записи об аренде.
     *
     * @param tenant  арендатор
     * @param product объект аренды
     */
    public AbstractRent(U tenant, P product) {
        this.tenant = tenant;
        this.product = product;
        this.startRental = Instant.now();
        this.status = RentStatus.ACTIVE;
    }
}
