package antifraud.service;

import antifraud.entity.suspiciousIP;

import java.util.List;
import java.util.Map;

public interface ipService {

    suspiciousIP addNewIp(Map<String, String> ip);

    void deleteIp(String ip);

    List<suspiciousIP> getAllSuspiciousIps();

}
