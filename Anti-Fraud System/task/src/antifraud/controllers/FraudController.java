package antifraud.controllers;

import antifraud.entity.Amount;
import antifraud.entity.Feedback;
import antifraud.entity.User;
import antifraud.request.accessRequest;
import antifraud.request.roleRequest;
import antifraud.response.Response;
import antifraud.service.TransactionService.TransactionService;
import antifraud.service.userService;
import antifraud.web.Result;
import antifraud.web.putFb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class FraudController {

    private final userService userService;

    private final TransactionService transactionService;

    public FraudController(antifraud.service.userService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PutMapping("/antifraud/transaction")
    public Feedback addFeedback(@RequestBody putFb fb) {

        long transactionId = fb.getTransactionId();

        String feedback = fb.getFeedback();

        return transactionService.addNewFeedback(transactionId, feedback);

    }

    @GetMapping("/antifraud/history/{number}")
    public List<Feedback> getHistoryByNumber(@PathVariable String number) {
        return transactionService.getHistoryByCardNumber(number);
    }

    @GetMapping("/antifraud/history")
    public ResponseEntity<List<Feedback>> getHistory() {
        List<Feedback> feedbacks = transactionService.getFullHistory();
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping("/antifraud/transaction")
    public ResponseEntity<Result> transaction(@RequestBody @Valid Amount amount) {
        return ResponseEntity.ok(transactionService.makeTransaction(amount));
    }

    @PostMapping("/auth/user")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @GetMapping("/auth/list")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/auth/user/{username}")
    public ResponseEntity<Response> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(new Response(username, "Deleted successfully!"));
    }

    @PutMapping("/auth/role")
    public User changeRole(@RequestBody roleRequest roleRequest) {
        String username = roleRequest.getUsername();
        String role = roleRequest.getRole();

        return userService.changeUserRole(username, role);
    }

    @PutMapping("/auth/access")
    public Map<String, String> changeStatus(@RequestBody accessRequest accessRequest) {

        String username = accessRequest.getUsername();
        String operation = accessRequest.getOperation();

        return userService.changeUserStatus(username, operation);
    }

}
