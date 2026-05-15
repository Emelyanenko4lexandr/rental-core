package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Базовый репозиторий для работы с объектами аренды.
 *
 * <p>Предоставляет готовые методы для наиболее типичных запросов:
 * поиск свободных объектов, поиск по владельцу, поиск по статусу.</p>
 *
 * <h3>Пример расширения для сервиса аренды авто:</h3>
 * <pre>{@code
 * public interface AutomobileRepository
 *         extends BaseRentalProductRepository<Automobile, User> {
 *
 *     // Специфичный для авто поиск по номеру
 *     Optional<Automobile> findByRegistrationNumber(String number);
 * }
 * }</pre>
 *
 * @param <P> тип объекта аренды, должен расширять {@link AbstractRentalProduct}
 * @param <U> тип пользователя-владельца, должен расширять {@link AbstractUser}
 */
@NoRepositoryBean
public interface BaseRentalProductRepository<P extends AbstractRentalProduct, U extends AbstractUser>
        extends JpaRepository<P, Long> {

    /**
     * Найти все объекты с указанным статусом.
     *
     * @param status статус объекта
     * @return список объектов с указанным статусом
     */
    List<P> findByStatus(ProductStatus status);

    /**
     * Найти все активные (не удалённые) объекты указанного владельца.
     *
     * @param ownerId идентификатор владельца
     * @return список активных объектов владельца
     */
    @Query("SELECT p FROM #{#entityName} p WHERE p.owner.id = :ownerId AND p.status != 'DELETED'")
    List<P> findActiveByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Найти объект по идентификатору и статусу.
     * Удобно для проверки доступности перед арендой.
     *
     * @param id     идентификатор объекта
     * @param status ожидаемый статус
     * @return объект, если найден с указанным статусом
     */
    Optional<P> findByIdAndStatus(Long id, ProductStatus status);
}
