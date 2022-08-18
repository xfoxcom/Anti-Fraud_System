package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.time.LocalDateTime;
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

@PostMapping("/api/antifraud/transaction")
public ResponseEntity<Result> transaction (@RequestBody @Valid Amount amount) {
    if (!AntiFraudController.isLuhn(amount.getNumber())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    if (!amount.isValidRegion()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    List<String> reasons = new ArrayList<>();

    List<Amount> trans = transactionRepository.findAllByNumberAndDateBetween(amount.getNumber(), amount.getDate().minusHours(1), amount.getDate());

    long countIP = trans.stream().map(Amount::getIp).distinct().filter(i -> !i.equals(amount.getIp())).count();
    long countRegion = trans.stream().map(Amount::getRegion).distinct().filter(i -> !i.equals(amount.getRegion())).count();

    if (Cards.existsByNumber(amount.getNumber())) reasons.add("card-number");
    if (IPs.existsByIp(amount.getIp())) reasons.add("ip");
    if (amount.getAmount() > 200) reasons.add("amount");
    if (countIP >= 2) reasons.add("ip-correlation");
    if (countRegion >= 2) reasons.add("region-correlation");

    if (amount.getAmount() <= 200 & reasons.isEmpty()) {
        transactionRepository.save(amount);
        return ResponseEntity.ok(new Result("ALLOWED", "none"));
    }

    reasons.sort(Comparator.naturalOrder());

    if (reasons.contains("ip") | reasons.contains("card-number") | amount.getAmount() > 1500 | countRegion > 2 | countIP > 2) {
        transactionRepository.save(amount);
        if (amount.getAmount() < 1500) reasons.remove("amount");
        return ResponseEntity.ok(new Result("PROHIBITED", String.join(", ", reasons)));
    }

    transactionRepository.save(amount);
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
public long checkIp(Amount amount) {
   List<Amount> trans = transactionRepository.findAllByNumberAndDateBetween(amount.getNumber(), amount.getDate().minusHours(1), amount.getDate());
   return trans.stream().filter(number -> !Cards.existsByNumber(number.getNumber())).map(Amount::getIp).filter(ip -> !IPs.existsByIp(ip)).distinct().filter(i -> !i.equals(amount.getIp())).count();
}
public long checkRegion(Amount amount) {
    List<Amount> trans = transactionRepository.findAllByNumberAndDateBetween(amount.getNumber(), amount.getDate().minusHours(1), amount.getDate());
    return trans.stream().filter(number -> !Cards.existsByNumber(number.getNumber())).filter(ip -> !IPs.existsByIp(ip.getIp())).map(Amount::getRegion).distinct().count();
    }
}
