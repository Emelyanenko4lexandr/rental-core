package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractRent;
import dev.rentalcore.entity.AbstractRentalProduct;
import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.entity.enums.RentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRentRepository<
        R extends AbstractRent,
        P extends AbstractRentalProduct,
        U extends AbstractUser>
        extends JpaRepository<R, Long> {


    List<R> findByTenantAndStatus(U tenant, RentStatus status);


    Optional<R> findByProductAndTenantAndStatus(P product, U tenant, RentStatus status);

    List<R> findByStatus(RentStatus status);
}
