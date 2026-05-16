package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractRentalProduct<U extends AbstractUser> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.PENDING;

    private Double rating;

    @Column(nullable = false)
    private Double pricePerUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private U owner;

    public AbstractRentalProduct(String name, String description, Double pricePerUnit, U owner) {
        this.name = name;
        this.description = description;
        this.pricePerUnit = pricePerUnit;
        this.owner = owner;
        this.status = ProductStatus.PENDING;
    }
}
