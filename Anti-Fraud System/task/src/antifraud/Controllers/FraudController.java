package antifraud.Controllers;

import antifraud.Entity.Amount;
import antifraud.Entity.Feedback;
import antifraud.Entity.User;
import antifraud.Request.accessRequest;
import antifraud.Request.roleRequest;
import antifraud.Response.Response;
import antifraud.Service.TransactionService.TransactionService;
import antifraud.Service.userService;
import antifraud.Web.Result;
import antifraud.Web.putFb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
public class FraudController {

    private final userService userService;

    private final TransactionService transactionService;

    public FraudController(antifraud.Service.userService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PutMapping("/api/antifraud/transaction")
    public Feedback addFeedback(@RequestBody putFb fb) {

        long transactionId = fb.getTransactionId();

        String feedback = fb.getFeedback();

        return transactionService.addNewFeedback(transactionId, feedback);

    }

    @GetMapping("/api/antifraud/history/{number}")
    public List<Feedback> getHistoryByNumber(@PathVariable String number) {
        return transactionService.getHistoryByCardNumber(number);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<List<Feedback>> getHistory() {
        List<Feedback> feedbacks = transactionService.getFullHistory();
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<Result> transaction(@RequestBody @Valid Amount amount) {
        return ResponseEntity.ok(transactionService.makeTransaction(amount));
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Response> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(new Response(username, "Deleted successfully!"));
    }

    @PutMapping("/api/auth/role")
    public User changeRole(@RequestBody roleRequest roleRequest) {
        String username = roleRequest.getUsername();
        String role = roleRequest.getRole();

        return userService.changeUserRole(username, role);
    }

    @PutMapping("/api/auth/access")
    public Map<String, String> changeStatus(@RequestBody accessRequest accessRequest) {

        String username = accessRequest.getUsername();
        String operation = accessRequest.getOperation();

        return userService.changeUserStatus(username, operation);
    }

}
