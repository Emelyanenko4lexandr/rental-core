package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Абстрактный базовый класс объекта аренды.
 *
 * <p>Представляет любой объект, который может быть предложен в аренду:
 * автомобиль, жилое помещение, строительное оборудование и т.д.
 * Клиент наследует этот класс и добавляет поля, специфичные для
 * своей предметной области.</p>
 *
 * <p>Класс параметризован типом пользователя {@code U}, что позволяет
 * обеспечить строгую типизацию связи между объектом аренды и его
 * владельцем в конкретном проекте клиента.</p>
 *
 * <h3>Пример расширения для сервиса аренды автомобилей:</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "automobiles")
 * public class Automobile extends AbstractRentalProduct<User> {
 *
 *     private String brand;
 *     private String model;
 *     private String registrationNumber;
 * }
 * }</pre>
 *
 * <h3>Пример расширения для сервиса аренды жилья:</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "apartments")
 * public class Apartment extends AbstractRentalProduct<User> {
 *
 *     private String address;
 *     private Integer rooms;
 *     private Double area;
 * }
 * }</pre>
 *
 * @param <U> тип пользователя-владельца, должен расширять {@link AbstractUser}
 * @see ProductStatus
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractRentalProduct<U extends AbstractUser> {

    /**
     * Уникальный идентификатор объекта аренды.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Название объекта аренды.
     * Для автомобиля — марка и модель, для квартиры — заголовок объявления.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Описание объекта аренды.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Текущий статус объекта.
     *
     * @see ProductStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.PENDING;

    /**
     * Рейтинг объекта аренды.
     * Вычисляется на основе оценок арендаторов.
     * Может быть {@code null} если объект ещё не получал оценок.
     */
    private Double rating;

    /**
     * Стоимость аренды за единицу времени (час/сутки — определяет клиент).
     */
    @Column(nullable = false)
    private Double pricePerUnit;

    /**
     * Владелец объекта аренды (арендодатель).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private U owner;

    /**
     * Конструктор для создания объекта аренды с основными параметрами.
     *
     * @param name         название объекта
     * @param description  описание объекта
     * @param pricePerUnit стоимость за единицу времени
     * @param owner        владелец объекта
     */
    public AbstractRentalProduct(String name, String description, Double pricePerUnit, U owner) {
        this.name = name;
        this.description = description;
        this.pricePerUnit = pricePerUnit;
        this.owner = owner;
        this.status = ProductStatus.PENDING;
    }
}
