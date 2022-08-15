package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StolenCardsRepository extends JpaRepository<StolenCard, Long> {
    boolean existsByNumber(String number);
    @Transactional
    void deleteByNumber(String number);
}
