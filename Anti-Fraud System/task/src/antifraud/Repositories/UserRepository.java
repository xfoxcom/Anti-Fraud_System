package antifraud.Repositories;

import antifraud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    void deleteByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String name);
    Optional<User> findByUsernameIgnoreCase(String username);
}
