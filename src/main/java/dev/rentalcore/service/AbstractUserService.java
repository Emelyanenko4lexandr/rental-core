package dev.rentalcore.service;

import dev.rentalcore.entity.AbstractUser;
import dev.rentalcore.exception.RentalBadRequestException;
import dev.rentalcore.exception.RentalNotFoundException;
import dev.rentalcore.repository.BaseUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractUserService<U extends AbstractUser> {

    protected final BaseUserRepository<U> userRepository;

    protected AbstractUserService(BaseUserRepository<U> userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public U getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public List<U> getAllActive() {
        return userRepository.findByEnabledTrue();
    }

    @Transactional
    public Double addBalance(Long id, Double sum) {
        if (sum == null || sum <= 0) {
            throw new RentalBadRequestException("Amount must be greater than zero");
        }
        U user = getById(id);
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
        return user.getBalance();
    }

    @Transactional
    public void disableUser(Long id) {
        U user = getById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long id) {
        U user = getById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public abstract U register(String fullName, String password);
}
