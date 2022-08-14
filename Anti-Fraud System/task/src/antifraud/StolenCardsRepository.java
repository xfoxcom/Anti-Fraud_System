package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StolenCardsRepository extends JpaRepository<StolenCard, Long> {
    boolean existsByNumber(String number);
    void deleteByNumber(String number);
}
