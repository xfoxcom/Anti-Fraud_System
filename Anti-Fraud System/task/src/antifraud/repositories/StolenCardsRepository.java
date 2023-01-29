package antifraud.repositories;

import antifraud.entity.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StolenCardsRepository extends JpaRepository<StolenCard, Long> {
    boolean existsByNumber(String number);

    void deleteByNumber(String number);
}
