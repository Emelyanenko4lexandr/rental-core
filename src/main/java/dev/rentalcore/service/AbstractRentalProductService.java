package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.ProductStatus;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseRentalProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractRentalProductService<
        P extends AbstractRentalProduct,
        U extends AbstractUser> {

    protected final BaseRentalProductRepository<P, U> productRepository;

    protected AbstractRentalProductService(BaseRentalProductRepository<P, U> productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<P> getAvailable() {
        return productRepository.findByStatus(ProductStatus.FREE);
    }

    @Transactional(readOnly = true)
    public List<P> getByOwner(Long ownerId) {
        return productRepository.findActiveByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public P getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException("Product", id));
    }

    @Transactional
    public void delete(Long id) {
        P product = getById(id);
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }


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

    protected void onProductVerified(P product) {
    }

    public abstract P publish(U owner);
}
