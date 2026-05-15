package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractRent;
import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.RentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Базовый репозиторий для работы с записями об аренде.
 *
 * <h3>Пример расширения:</h3>
 * <pre>{@code
 * public interface RentRepository
 *         extends BaseRentRepository<Rent, Automobile, User> {
 *
 *     // Специфичный запрос: аренды за определённый период
 *     List<Rent> findByStartRentalBetween(Instant from, Instant to);
 * }
 * }</pre>
 *
 * @param <R> тип записи об аренде, должен расширять {@link AbstractRent}
 * @param <P> тип объекта аренды, должен расширять {@link AbstractRentalProduct}
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 */
@NoRepositoryBean
public interface BaseRentRepository<
        R extends AbstractRent,
        P extends AbstractRentalProduct,
        U extends AbstractUser>
        extends JpaRepository<R, Long> {

    /**
     * Найти все аренды указанного арендатора с заданным статусом.
     *
     * @param tenant арендатор
     * @param status статус аренды
     * @return список аренд
     */
    List<R> findByTenantAndStatus(U tenant, RentStatus status);

    /**
     * Найти конкретную аренду по объекту, арендатору и статусу.
     * Используется для проверки наличия активной аренды перед созданием новой.
     *
     * @param product объект аренды
     * @param tenant  арендатор
     * @param status  статус аренды
     * @return аренда, если найдена
     */
    Optional<R> findByProductAndTenantAndStatus(P product, U tenant, RentStatus status);

    /**
     * Найти все аренды с указанным статусом.
     *
     * @param status статус аренды
     * @return список аренд
     */
    List<R> findByStatus(RentStatus status);
}
