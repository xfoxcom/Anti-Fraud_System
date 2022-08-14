package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class AntiFraudController {

    public static final String ipPattern = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    @Autowired
    suspIPsRepository IPs;

    @Autowired
    StolenCardsRepository Cards;

    @PostMapping("api/antifraud/suspicious-ip")
    public ResponseEntity<suspiciousIP> postIP(@RequestBody Map<String, String> ip) {
        if (!ip.get("ip").matches(ipPattern)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (IPs.existsByIp(ip.get("ip"))) throw new ResponseStatusException(HttpStatus.CONFLICT);
        suspiciousIP suspiciousIP = new suspiciousIP();
        suspiciousIP.setIp(ip.get("ip"));
        IPs.save(suspiciousIP);
        return new ResponseEntity<>(suspiciousIP, HttpStatus.CREATED);
    }
    @DeleteMapping("api/antifraud/suspicious-ip/{ip}")
    public Map<String, String> deleteIP(@PathVariable String ip) {
        if (!ip.matches(ipPattern)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!IPs.existsByIp(ip)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        IPs.deleteByIp(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }
    @GetMapping("api/antifraud/suspicious-ip")
    public List<suspiciousIP> getIPs() {
        List<suspiciousIP> listOfIp = IPs.findAll();
        listOfIp.sort(Comparator.comparing(suspiciousIP::getId));
        return listOfIp;
    }

    @PostMapping("api/antifraud/stolencard")
    public ResponseEntity<StolenCard> postStolenCard(@RequestBody StolenCard stolenCard) {
        if (Cards.existsByNumber(stolenCard.getNumber())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (!isLuhn(stolenCard.getNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(stolenCard, HttpStatus.CREATED);
    }
    @DeleteMapping("api/antifraud/stolencard/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {
        if (!Cards.existsByNumber(number)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!isLuhn(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Cards.deleteByNumber(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }
    @GetMapping("api/antifraud/stolencard")
    public List<StolenCard> getStolenCards() {
        List<StolenCard> list = Cards.findAll();
        list.sort(Comparator.comparing(StolenCard::getId));
        return list;
    }

    public static boolean isLuhn (String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }
}
