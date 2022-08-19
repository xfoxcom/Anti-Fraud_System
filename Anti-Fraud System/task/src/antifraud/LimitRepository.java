package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LimitRepository extends JpaRepository<Limits, Long> {
}
