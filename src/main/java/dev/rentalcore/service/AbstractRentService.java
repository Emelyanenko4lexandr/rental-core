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

    @Transactional(readOnly = true)
    public List<R> getActiveByTenant(U tenant) {
        return rentRepository.findByTenantAndStatus(tenant, RentStatus.ACTIVE);
    }


    @Transactional(readOnly = true)
    public List<R> getHistoryByTenant(U tenant) {
        return rentRepository.findByTenantAndStatus(tenant, RentStatus.FINISHED);
    }

    @Transactional(readOnly = true)
    public List<R> getAll() {
        return rentRepository.findAll();
    }

    protected void onRentStarted(R rent) {
    }

    protected void onRentFinished(R rent) {
    }

    protected abstract R createRentInstance(U tenant, P product);
}
