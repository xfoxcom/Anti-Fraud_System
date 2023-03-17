package antifraud.repositories;

import antifraud.entity.Limits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LimitRepository extends JpaRepository<Limits, Long> {
}
