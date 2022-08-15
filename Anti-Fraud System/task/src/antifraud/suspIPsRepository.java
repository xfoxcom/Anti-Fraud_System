package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface suspIPsRepository extends JpaRepository<suspiciousIP, Long> {
    boolean existsByIp(String ip);
    @Transactional
    void deleteByIp(String ip);
}
