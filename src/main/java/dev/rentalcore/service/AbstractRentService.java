package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractRent;
import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.ProductStatus;
import dev.rentalcore.entity.enums.RentStatus;
import dev.rentalcore.exception.RentalBadRequestException;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseRentRepository;
import dev.rentalcore.repository.BaseRentalProductRepository;
import dev.rentalcore.repository.BaseUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Абстрактный сервис управления арендой.
 *
 * <p>Реализует ключевую бизнес-логику: создание аренды, завершение аренды,
 * получение истории. Использует паттерн Template Method: базовый алгоритм
 * зафиксирован, а хуки {@link #onRentStarted} и {@link #onRentFinished}
 * позволяют клиенту встраивать дополнительную логику без изменения
 * основного потока выполнения.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Service
 * public class RentService extends AbstractRentService<Rent, Automobile, User> {
 *
 *     public RentService(RentRepository rentRepo,
 *                        AutomobileRepository productRepo,
 *                        UserRepository userRepo) {
 *         super(rentRepo, productRepo, userRepo);
 *     }
 *
 *     @Override
 *     protected Rent createRentInstance(User tenant, Automobile product) {
 *         return new Rent(tenant, product);
 *     }
 *
 *     // Хук: списать деньги с баланса после начала аренды
 *     @Override
 *     protected void onRentStarted(Rent rent) {
 *         User tenant = rent.getTenant();
 *         tenant.setBalance(tenant.getBalance() - rent.getProduct().getPricePerUnit());
 *         userRepository.save(tenant);
 *     }
 *
 *     // Хук: начислить рейтинг после завершения
 *     @Override
 *     protected void onRentFinished(Rent rent) {
 *         ratingService.calculateAndSave(rent.getProduct().getId());
 *     }
 * }
 * }</pre>
 *
 * @param <R> тип аренды, должен расширять {@link AbstractRent}
 * @param <P> тип объекта аренды, должен расширять {@link AbstractRentalProduct}
 * @param <U> тип пользователя, должен расширять {@link AbstractUser}
 */
public abstract class AbstractRentService<
        R extends AbstractRent,
        P extends AbstractRentalProduct,
        U extends AbstractUser> {

    protected final BaseRentRepository<R, P, U> rentRepository;
    protected final BaseRentalProductRepository<P, U> productRepository;
    protected final BaseUserRepository<U> userRepository;

    protected AbstractRentService(
            BaseRentRepository<R, P, U> rentRepository,
            BaseRentalProductRepository<P, U> productRepository,
            BaseUserRepository<U> userRepository) {
        this.rentRepository = rentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // -----------------------------------------------------------------------
    // Готовые методы
    // -----------------------------------------------------------------------

    /**
     * Начать аренду объекта.
     *
     * <p>Алгоритм:</p>
     * <ol>
     *   <li>Проверить, что объект существует и имеет статус FREE</li>
     *   <li>Создать запись об аренде через {@link #createRentInstance}</li>
     *   <li>Установить объекту статус USED</li>
     *   <li>Сохранить изменения</li>
     *   <li>Вызвать хук {@link #onRentStarted}</li>
     * </ol>
     *
     * @param productId идентификатор объекта аренды
     * @param tenantId  идентификатор арендатора
     * @return созданная запись об аренде
     * @throws RentalNotFoundException   если объект или пользователь не найден
     * @throws RentalBadRequestException если объект недоступен для аренды
     */
    @Transactional
    public R startRent(Long productId, Long tenantId) {
        P product = productRepository.findByIdAndStatus(productId, ProductStatus.FREE)
                .orElseThrow(() -> new RentalNotFoundException(
                        "Product with id " + productId + " not found or not available"));

        U tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RentalNotFoundException("User", tenantId));

        if (product.getStatus() != ProductStatus.FREE) {
            throw new RentalBadRequestException("Product is not available for rent");
        }

        R rent = createRentInstance(tenant, product);
        rentRepository.save(rent);

        product.setStatus(ProductStatus.USED);
        productRepository.save(product);

        onRentStarted(rent);

        return rent;
    }

    /**
     * Завершить аренду.
     *
     * <p>Алгоритм:</p>
     * <ol>
     *   <li>Найти активную запись об аренде</li>
     *   <li>Установить время завершения и статус FINISHED</li>
     *   <li>Вернуть объекту статус FREE</li>
     *   <li>Сохранить изменения</li>
     *   <li>Вызвать хук {@link #onRentFinished}</li>
     * </ol>
     *
     * @param rentId идентификатор записи об аренде
     * @return завершённая запись об аренде
     * @throws RentalNotFoundException если аренда не найдена или уже завершена
     */
    @Transactional
    public R endRent(Long rentId) {
        R rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RentalNotFoundException("Rent", rentId));

        if (rent.getStatus() == RentStatus.FINISHED) {
            throw new RentalBadRequestException("Rent with id " + rentId + " is already finished");
        }

        rent.setEndRental(Instant.now());
        rent.setStatus(RentStatus.FINISHED);

        P product = (P) rent.getProduct();
        product.setStatus(ProductStatus.FREE);
        productRepository.save(product);

        rentRepository.save(rent);

        onRentFinished(rent);

        return rent;
    }

    /**
     * Получить все активные аренды указанного арендатора.
     *
     * @param tenant арендатор
     * @return список активных аренд
     */
    @Transactional(readOnly = true)
    public List<R> getActiveByTenant(U tenant) {
        return rentRepository.findByTenantAndStatus(tenant, RentStatus.ACTIVE);
    }

    /**
     * Получить полную историю аренд указанного арендатора.
     *
     * @param tenant арендатор
     * @return список всех завершённых аренд
     */
    @Transactional(readOnly = true)
    public List<R> getHistoryByTenant(U tenant) {
        return rentRepository.findByTenantAndStatus(tenant, RentStatus.FINISHED);
    }

    /**
     * Получить все аренды в системе (для администратора).
     *
     * @return список всех аренд
     */
    @Transactional(readOnly = true)
    public List<R> getAll() {
        return rentRepository.findAll();
    }

    // -----------------------------------------------------------------------
    // Хуки — клиент переопределяет при необходимости
    // -----------------------------------------------------------------------

    /**
     * Хук, вызываемый после успешного создания аренды.
     *
     * <p>По умолчанию ничего не делает. Переопределите для добавления
     * логики: списание средств, отправка уведомления, запись в лог.</p>
     *
     * @param rent созданная запись об аренде
     */
    protected void onRentStarted(R rent) {
        // хук — переопределяется клиентом при необходимости
    }

    /**
     * Хук, вызываемый после завершения аренды.
     *
     * <p>По умолчанию ничего не делает. Переопределите для добавления
     * логики: расчёт стоимости, начисление рейтинга, отправка чека.</p>
     *
     * @param rent завершённая запись об аренде
     */
    protected void onRentFinished(R rent) {
        // хук — переопределяется клиентом при необходимости
    }

    // -----------------------------------------------------------------------
    // Абстрактные методы — обязательны к реализации клиентом
    // -----------------------------------------------------------------------

    /**
     * Создать экземпляр конкретного класса аренды.
     *
     * <p>Метод оставлен абстрактным, так как библиотека не знает
     * о конкретном классе-наследнике {@link AbstractRent}, который
     * определяет клиент. Клиент просто возвращает {@code new MyRent(tenant, product)}.</p>
     *
     * @param tenant  арендатор
     * @param product объект аренды
     * @return новый экземпляр аренды
     */
    protected abstract R createRentInstance(U tenant, P product);
}
