package antifraud.Service.serviceImpl;

import antifraud.Entity.suspiciousIP;
import antifraud.Repositories.suspIPsRepository;
import antifraud.Service.ipService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ipServiceImpl implements ipService {

    public static final String ipPattern = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    private final suspIPsRepository suspIPsRepository;

    public ipServiceImpl(suspIPsRepository suspIPsRepository) {
        this.suspIPsRepository = suspIPsRepository;
    }

    @Override
    public suspiciousIP addNewIp(Map<String, String> request) {

        String ip = request.get("ip");

        if (!ip.matches(ipPattern)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (suspIPsRepository.existsByIp(ip)) throw new ResponseStatusException(HttpStatus.CONFLICT);

        suspiciousIP suspiciousIP = new suspiciousIP();
        suspiciousIP.setIp(ip);
        suspIPsRepository.save(suspiciousIP);

        return suspiciousIP;
    }

    @Override
    public void deleteIp(String ip) {

        if (!ip.matches(ipPattern)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!suspIPsRepository.existsByIp(ip)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        suspIPsRepository.deleteByIp(ip);

    }

    @Override
    public List<suspiciousIP> getAllSuspiciousIps() {

        List<suspiciousIP> listOfIp = suspIPsRepository.findAll();
        listOfIp.sort(Comparator.comparing(suspiciousIP::getId));
        return listOfIp;

    }
}
