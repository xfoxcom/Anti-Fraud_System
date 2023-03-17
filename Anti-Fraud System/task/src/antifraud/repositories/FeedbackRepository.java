package antifraud.repositories;

import antifraud.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByNumber(String number);

    Optional<Feedback> findByTransactionId(long id);
}
