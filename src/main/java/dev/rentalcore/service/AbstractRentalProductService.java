package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.ProductStatus;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseRentalProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Абстрактный сервис для работы с объектами аренды.
 *
 * <p>Реализует базовую логику управления объектами: получение доступных,
 * мягкое удаление, верификация. Клиент наследует и добавляет логику
 * публикации с полями, специфичными для своей предметной области.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Service
 * public class AutoService extends AbstractRentalProductService<Automobile, User> {
 *
 *     public AutoService(AutomobileRepository repository) {
 *         super(repository);
 *     }
 *
 *     // Обязательный к реализации метод публикации
 *     @Override
 *     public Automobile publish(User owner, String brand, String model, ...) {
 *         Automobile auto = new Automobile(brand, model, owner);
 *         return productRepository.save(auto);
 *     }
 *
 *     // Хук вызывается после верификации — можно отправить уведомление
 *     @Override
 *     protected void onProductVerified(Automobile product) {
 *         notificationService.notifyOwner(product.getOwner(), "Верификация пройдена");
 *     }
 * }
 * }</pre>
 *
 * @param <P> тип объекта аренды, должен расширять {@link AbstractRentalProduct}
 * @param <U> тип пользователя-владельца, должен расширять {@link AbstractUser}
 */
public abstract class AbstractRentalProductService<
        P extends AbstractRentalProduct,
        U extends AbstractUser> {

    protected final BaseRentalProductRepository<P, U> productRepository;

    protected AbstractRentalProductService(BaseRentalProductRepository<P, U> productRepository) {
        this.productRepository = productRepository;
    }

    // -----------------------------------------------------------------------
    // Готовые методы
    // -----------------------------------------------------------------------

    /**
     * Получить все объекты со статусом FREE (доступные для аренды).
     *
     * @return список доступных объектов
     */
    @Transactional(readOnly = true)
    public List<P> getAvailable() {
        return productRepository.findByStatus(ProductStatus.FREE);
    }

    /**
     * Получить все активные (не удалённые) объекты конкретного владельца.
     *
     * @param ownerId идентификатор владельца
     * @return список объектов владельца
     */
    @Transactional(readOnly = true)
    public List<P> getByOwner(Long ownerId) {
        return productRepository.findActiveByOwnerId(ownerId);
    }

    /**
     * Найти объект по идентификатору.
     *
     * @param id идентификатор объекта
     * @return найденный объект
     * @throws RentalNotFoundException если объект не найден
     */
    @Transactional(readOnly = true)
    public P getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException("Product", id));
    }

    /**
     * Мягкое удаление объекта: устанавливает статус {@link ProductStatus#DELETED}.
     * Запись остаётся в базе данных, но не отображается в списках.
     *
     * @param id идентификатор объекта
     * @throws RentalNotFoundException если объект не найден
     */
    @Transactional
    public void delete(Long id) {
        P product = getById(id);
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    /**
     * Верифицировать объект: переводит статус из {@link ProductStatus#PENDING}
     * в {@link ProductStatus#FREE}, делая объект доступным для аренды.
     * После верификации вызывает хук {@link #onProductVerified(AbstractRentalProduct)}.
     *
     * @param id идентификатор объекта
     * @return верифицированный объект
     * @throws RentalNotFoundException если объект не найден или не в статусе PENDING
     */
    @Transactional
    public P verify(Long id) {
        P product = productRepository.findByIdAndStatus(id, ProductStatus.PENDING)
                .orElseThrow(() -> new RentalNotFoundException(
                        "Product with id " + id + " not found or not in PENDING status"));
        product.setStatus(ProductStatus.FREE);
        productRepository.save(product);
        onProductVerified(product);
        return product;
    }

    // -----------------------------------------------------------------------
    // Хуки — клиент переопределяет при необходимости
    // -----------------------------------------------------------------------

    /**
     * Хук, вызываемый после успешной верификации объекта.
     *
     * <p>По умолчанию ничего не делает. Клиент переопределяет этот метод
     * для добавления логики после верификации: отправка уведомления
     * владельцу, запись в лог, и т.д.</p>
     *
     * @param product верифицированный объект
     */
    protected void onProductVerified(P product) {
        // хук — переопределяется клиентом при необходимости
    }

    // -----------------------------------------------------------------------
    // Абстрактные методы — обязательны к реализации клиентом
    // -----------------------------------------------------------------------

    /**
     * Опубликовать новый объект аренды.
     *
     * <p>Метод оставлен абстрактным: набор полей при публикации
     * полностью определяется предметной областью клиента.
     * Автомобиль требует марку, модель, номер; квартира — адрес,
     * площадь, количество комнат.</p>
     *
     * @param owner владелец объекта
     * @return созданный объект
     */
    public abstract P publish(U owner);
}
