package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.List;


@RestController
public class FraudController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

@PostMapping("/api/antifraud/transaction")
public ResponseEntity<Result> transaction (@RequestBody @Valid Amount amount) {
         if (amount.getAmount() <= 200) return ResponseEntity.ok(new Result("ALLOWED"));
         if (amount.getAmount() <= 1500) return ResponseEntity.ok(new Result("MANUAL_PROCESSING"));
         return ResponseEntity.ok(new Result("PROHIBITED"));
}
@PostMapping("/api/auth/user")
public ResponseEntity<User> register(@RequestBody @Valid User user) {
    if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    user.setRole("ROLE_USER");
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
}
