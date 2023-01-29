package antifraud.controllers;

import antifraud.entity.suspiciousIP;
import antifraud.service.ipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/antifraud/suspicious-ip")
public class IpController {

    private final ipService ipService;

    @PostMapping
    public ResponseEntity<suspiciousIP> postIP(@RequestBody Map<String, String> ip) {
        return new ResponseEntity<>(ipService.addNewIp(ip), HttpStatus.OK);
    }

    @DeleteMapping("/{ip}")
    public Map<String, String> deleteIP(@PathVariable String ip) {
        ipService.deleteIp(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }

    @GetMapping
    public List<suspiciousIP> getIPs() {
        return ipService.getAllSuspiciousIps();
    }
}
