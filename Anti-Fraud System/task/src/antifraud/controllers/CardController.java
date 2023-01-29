package antifraud.controllers;

import antifraud.entity.StolenCard;
import antifraud.service.cardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/antifraud/stolencard")
public class CardController {

    private final cardService cardService;

    @PostMapping
    public ResponseEntity<StolenCard> postStolenCard(@RequestBody StolenCard stolenCard) {
        return new ResponseEntity<>(cardService.postNewCard(stolenCard), HttpStatus.OK);
    }

    @DeleteMapping("/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {
        cardService.deleteCard(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }

    @GetMapping
    public List<StolenCard> getStolenCards() {
        return cardService.getAllCards();
    }
}
