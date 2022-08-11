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
public User register(@RequestBody @Valid User user) {
    if (userRepository.existsById(user.getId())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return user;
}
@GetMapping("/api/auth/list")
public List<User> getAllUsers () {
    return userRepository.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "id"));
}
@DeleteMapping("/api/auth/user/{username}")
public ResponseEntity<Object> deleteUser (@PathVariable String username) {
    if (userRepository.existsByUsernameIgnoreCase(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    userRepository.deleteByUsernameIgnoreCase(username);
    class Response {
        String username;
        String status;
        public Response(String username, String status){
            this.username = username;
            this.status = status;
        }
    }
    return ResponseEntity.ok(new Response(userRepository.findByUsername(username).getUsername(), "Deleted successfully!"));
}
}
