package antifraud.controllers;

import antifraud.entity.StolenCard;
import antifraud.entity.suspiciousIP;
import antifraud.service.cardService;
import antifraud.service.ipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {

    private final ipService ipService;

    private final cardService cardService;

    public AntiFraudController(ipService ipService, cardService cardService) {
        this.ipService = ipService;
        this.cardService = cardService;
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<suspiciousIP> postIP(@RequestBody Map<String, String> ip) {
        return new ResponseEntity<>(ipService.addNewIp(ip), HttpStatus.OK);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public Map<String, String> deleteIP(@PathVariable String ip) {
        ipService.deleteIp(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }

    @GetMapping("/suspicious-ip")
    public List<suspiciousIP> getIPs() {
        return ipService.getAllSuspiciousIps();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCard> postStolenCard(@RequestBody StolenCard stolenCard) {
        return new ResponseEntity<>(cardService.postNewCard(stolenCard), HttpStatus.OK);
    }

    @DeleteMapping("/stolencard/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {
        cardService.deleteCard(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }

    @GetMapping("/stolencard")
    public List<StolenCard> getStolenCards() {
        return cardService.getAllCards();
    }

}
