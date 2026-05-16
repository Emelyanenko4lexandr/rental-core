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

@NoRepositoryBean
public interface BaseRentalProductRepository<P extends AbstractRentalProduct, U extends AbstractUser>
        extends JpaRepository<P, Long> {

    List<P> findByStatus(ProductStatus status);


    @Query("SELECT p FROM #{#entityName} p WHERE p.owner.id = :ownerId AND p.status != 'DELETED'")
    List<P> findActiveByOwnerId(@Param("ownerId") Long ownerId);

    Optional<P> findByIdAndStatus(Long id, ProductStatus status);
}
