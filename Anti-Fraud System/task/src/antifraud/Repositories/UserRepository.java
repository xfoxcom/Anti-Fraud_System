package antifraud.Repositories;

import antifraud.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameIgnoreCase(String name);
    Optional<User> findByUsernameIgnoreCase(String username);
}
