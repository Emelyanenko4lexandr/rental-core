package dev.rentalcore.entity;

import dev.rentalcore.entity.enums.BaseRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseRole role;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private boolean enabled = true;

    public AbstractUser(String fullName, BaseRole role, Double balance, boolean enabled) {
        this.fullName = fullName;
        this.role = role;
        this.balance = balance;
        this.enabled = enabled;
    }
}
