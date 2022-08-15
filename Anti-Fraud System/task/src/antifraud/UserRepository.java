package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    void deleteByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String name);
    User findByUsernameIgnoreCase(String username);
}
