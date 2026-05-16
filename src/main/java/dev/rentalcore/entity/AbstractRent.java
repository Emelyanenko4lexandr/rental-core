package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.RentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractRent<P extends AbstractRentalProduct, U extends AbstractUser> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private U tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private P product;

    @Column(nullable = false)
    private Instant startRental;

    private Instant endRental;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentStatus status;

    private Double totalCost;

    public AbstractRent(U tenant, P product) {
        this.tenant = tenant;
        this.product = product;
        this.startRental = Instant.now();
        this.status = RentStatus.ACTIVE;
    }
}
