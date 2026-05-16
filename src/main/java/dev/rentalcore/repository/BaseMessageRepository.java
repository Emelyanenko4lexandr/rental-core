package dev.rentalcore.repository;

import dev.rentalcore.entity.AbstractMessage;
import dev.rentalcore.entity.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseMessageRepository<M extends AbstractMessage, U extends AbstractUser>
        extends JpaRepository<M, Long> {

    List<M> findByRecipient(U recipient);

    List<M> findBySender(U sender);
}
