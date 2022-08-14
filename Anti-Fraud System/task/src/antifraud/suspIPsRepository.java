package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface suspIPsRepository extends JpaRepository<suspiciousIP, Long> {
    boolean existsByIp(String ip);
    void deleteByIp(String ip);
}
