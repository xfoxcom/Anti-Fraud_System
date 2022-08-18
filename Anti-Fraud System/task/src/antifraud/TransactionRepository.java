package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Amount, Long> {
    List<Amount> findAllByNumberAndDateBetween(String number, LocalDateTime start, LocalDateTime end);
}
