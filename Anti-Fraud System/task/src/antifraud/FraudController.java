package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@RestController
public class FraudController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    suspIPsRepository IPs;
    @Autowired
    StolenCardsRepository Cards;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    LimitRepository limits;

    @PutMapping("/api/antifraud/transaction")
    public Feedback addFeedback(@RequestBody putFb fb) {
        if (!transactionRepository.existsById(fb.getTransactionId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Feedback feedback = feedbackRepository.findByTransactionId(fb.getTransactionId());
        if (!fb.getFeedback().equals("ALLOWED") & !fb.getFeedback().equals("MANUAL_PROCESSING") & !fb.getFeedback().equals("PROHIBITED")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (!feedback.getFeedback().equals("")) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (feedback.getResult().equals(fb.getFeedback())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        Limits limit = limits.findById(1L).orElseThrow();

        if (feedback.getResult().equals("MANUAL_PROCESSING") & fb.getFeedback().equals("ALLOWED")) increaseLimit(limit, feedback.getAmount());
        if (feedback.getResult().equals("PROHIBITED") & fb.getFeedback().equals("ALLOWED")) {
            increaseMax(limit, feedback.getAmount());
            increaseLimit(limit, feedback.getAmount());
        }

        if (feedback.getResult().equals("ALLOWED") & fb.getFeedback().equals("MANUAL_PROCESSING")) decreaseLimit(limit, feedback.getAmount());
        if (feedback.getResult().equals("ALLOWED") & fb.getFeedback().equals("PROHIBITED")) {
            decreaseMax(limit, feedback.getAmount());
            decreaseLimit(limit, feedback.getAmount());
        }

        if (feedback.getResult().equals("PROHIBITED") & fb.getFeedback().equals("MANUAL_PROCESSING")) increaseMax(limit, feedback.getAmount());

        if (feedback.getResult().equals("MANUAL_PROCESSING") & fb.getFeedback().equals("PROHIBITED")) decreaseMax(limit, feedback.getAmount());

        feedback.setFeedback(fb.getFeedback());
        feedbackRepository.save(feedback);
        limits.save(limit);
        System.out.println(limit);
        return feedback;
    }
    @GetMapping("/api/antifraud/history/{number}")
    public List<Feedback> getHistoryByNumber(@PathVariable String number) {
        if (!AntiFraudController.isLuhn(number)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        List<Feedback> feedbacks = feedbackRepository.findAllByNumber(number);
        if (feedbacks.size() == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return feedbacks;
    }
    @GetMapping("/api/antifraud/history")
    public ResponseEntity<List<Feedback>> getHistory() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return ResponseEntity.ok(feedbacks);
    }
@PostMapping("/api/antifraud/transaction")
public ResponseEntity<Result> transaction (@RequestBody @Valid Amount amount) {
    if (!AntiFraudController.isLuhn(amount.getNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    if (!amount.isValidRegion()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    List<String> reasons = new ArrayList<>();

    if (limits.count() == 0) limits.save(new Limits(1, 200, 1500));
    long allowLimit = limits.findById(1L).get().getAllow();
    long maxLimit = limits.findById(1L).get().getMax();

    List<Amount> trans = transactionRepository.findAllByNumberAndDateBetween(amount.getNumber(), amount.getDate().minusHours(1), amount.getDate());

    long countIP = trans.stream().map(Amount::getIp).distinct().filter(i -> !i.equals(amount.getIp())).count();
    long countRegion = trans.stream().map(Amount::getRegion).distinct().filter(i -> !i.equals(amount.getRegion())).count();

    if (Cards.existsByNumber(amount.getNumber())) reasons.add("card-number");
    if (IPs.existsByIp(amount.getIp())) reasons.add("ip");
    if (amount.getAmount() > allowLimit) reasons.add("amount");
    if (countIP >= 2) reasons.add("ip-correlation");
    if (countRegion >= 2) reasons.add("region-correlation");

    Feedback feedback = new Feedback();
    feedback.setAmount(amount.getAmount());
    feedback.setDate(amount.getDate());
    feedback.setIp(amount.getIp());
    feedback.setNumber(amount.getNumber());
    feedback.setRegion(amount.getRegion());
    feedback.setFeedback("");

    if (amount.getAmount() <= allowLimit & reasons.isEmpty()) {
        transactionRepository.save(amount);
        feedback.setResult("ALLOWED");
        feedback.setTransactionId(amount.getId());
        feedbackRepository.save(feedback);
        return ResponseEntity.ok(new Result("ALLOWED", "none"));
    }

    reasons.sort(Comparator.naturalOrder());

    if (reasons.contains("ip") | reasons.contains("card-number") | amount.getAmount() > maxLimit | countRegion > 2 | countIP > 2) {  // 1500
        transactionRepository.save(amount);
        if (amount.getAmount() < maxLimit) reasons.remove("amount"); //1500
        feedback.setResult("PROHIBITED");
        feedback.setTransactionId(amount.getId());
        feedbackRepository.save(feedback);
        return ResponseEntity.ok(new Result("PROHIBITED", String.join(", ", reasons)));
    }
    transactionRepository.save(amount);
    feedback.setResult("MANUAL_PROCESSING");
    feedback.setTransactionId(amount.getId());
    feedbackRepository.save(feedback);
    return ResponseEntity.ok(new Result("MANUAL_PROCESSING", String.join(", ", reasons)));
}
@PostMapping("/api/auth/user")
public ResponseEntity<User> register(@RequestBody @Valid User user) {
    if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    if (userRepository.count() == 0) {
        user.setRole("ADMINISTRATOR");
        user.setAccountNonLocked(true);
    } else {
        user.setRole("MERCHANT");
        user.setAccountNonLocked(false);
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
}
@GetMapping("/api/auth/list")
public List<User> getAllUsers () {
    return userRepository.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "id"));
}
@DeleteMapping("/api/auth/user/{username}")
public ResponseEntity<Object> deleteUser (@PathVariable String username) {
    if (!userRepository.existsByUsernameIgnoreCase(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    User user = userRepository.findByUsernameIgnoreCase(username);
    userRepository.delete(user);
    return ResponseEntity.ok(new Response(user.getUsername(), "Deleted successfully!"));
}
@PutMapping("/api/auth/role")
public User changeRole(@RequestBody roleRequest roleRequest) {
    String username = roleRequest.getUsername();
    String role = roleRequest.getRole();
    if (!userRepository.existsByUsernameIgnoreCase(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    if (!roleRequest.getRole().equals("SUPPORT") & !roleRequest.getRole().equals("MERCHANT")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    User user = userRepository.findByUsernameIgnoreCase(username);
    if (user.getRole().equals(roleRequest.getRole())) throw new ResponseStatusException(HttpStatus.CONFLICT);
    if (user.getRole().equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    user.setRole(role);
    userRepository.save(user);
    return user;
}
@PutMapping("/api/auth/access")
public Map<String, String> changeStatus(@RequestBody accessRequest accessRequest) {
    if (!userRepository.existsByUsernameIgnoreCase(accessRequest.getUsername())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    User user = userRepository.findByUsernameIgnoreCase(accessRequest.getUsername());
    if (user.getRole().equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    String operation = accessRequest.getOperation();
    switch (operation) {
        case "LOCK": user.setAccountNonLocked(false);
        userRepository.save(user);
        return Map.of("status", "User " + user.getUsername() + " locked!");
        case "UNLOCK": user.setAccountNonLocked(true);
            userRepository.save(user);
            return Map.of("status", "User " + user.getUsername() + " unlocked!");
    }
    return Map.of();
}
public void increaseLimit(Limits limits, long value) {
        limits.setAllow ((long) Math.ceil(limits.getAllow()*0.8 + 0.2*value));
    }
public void decreaseLimit(Limits limits, long value) {
    limits.setAllow ((long) Math.ceil(limits.getAllow()*0.8 - 0.2*value));
}
    public void increaseMax(Limits limits, long value) {
        limits.setMax ((long) Math.ceil(limits.getMax()*0.8 + 0.2*value));
    }
    public void decreaseMax(Limits limits, long value) {
        limits.setMax ((long) Math.ceil(limits.getMax()*0.8 - 0.2*value));
    }
}
