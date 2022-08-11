package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    void deleteByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String name);
    User findByUsername(String username);
}
