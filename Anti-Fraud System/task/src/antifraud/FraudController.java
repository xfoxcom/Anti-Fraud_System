package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class FraudController {
@PostMapping("/api/antifraud/transaction")
public ResponseEntity<Result> transaction (@RequestBody @Valid Amount amount) {
         if (amount.getAmount() <= 200) return ResponseEntity.ok(new Result("ALLOWED"));
         if (amount.getAmount() <= 1500) return ResponseEntity.ok(new Result("MANUAL_PROCESSING"));
         return ResponseEntity.ok(new Result("PROHIBITED"));
}
}
