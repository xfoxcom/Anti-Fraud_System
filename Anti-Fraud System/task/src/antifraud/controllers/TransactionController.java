package antifraud.controllers;

import antifraud.entity.Amount;
import antifraud.entity.Feedback;
import antifraud.service.TransactionService.TransactionService;
import antifraud.web.Result;
import antifraud.web.putFb;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/antifraud")
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/transaction")
    public Feedback addFeedback(@RequestBody putFb fb) {

        long transactionId = fb.getTransactionId();

        String feedback = fb.getFeedback();

        return transactionService.addNewFeedback(transactionId, feedback);

    }

    @GetMapping("/history/{number}")
    public List<Feedback> getHistoryByNumber(@PathVariable String number) {
        return transactionService.getHistoryByCardNumber(number);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Feedback>> getHistory() {
        List<Feedback> feedbacks = transactionService.getFullHistory();
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping("/transaction")
    public ResponseEntity<Result> transaction(@RequestBody @Valid Amount amount) {
        return ResponseEntity.ok(transactionService.makeTransaction(amount));
    }

}
