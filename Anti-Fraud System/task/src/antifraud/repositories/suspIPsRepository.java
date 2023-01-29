package antifraud.repositories;

import antifraud.entity.suspiciousIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface suspIPsRepository extends JpaRepository<suspiciousIP, Long> {
    boolean existsByIp(String ip);

    void deleteByIp(String ip);
}
