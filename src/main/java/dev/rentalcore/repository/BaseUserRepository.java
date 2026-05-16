package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseUserRepository<U extends AbstractUser> extends JpaRepository<U, Long> {

    List<U> findByEnabledTrue();

    List<U> findByRole(String role);
}
