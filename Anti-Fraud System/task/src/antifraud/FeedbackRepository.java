package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByNumber(String number);
    boolean existsByTransactionId(long id);
    Feedback findByTransactionId(long id);
}
