package antifraud.Service.serviceImpl;

import antifraud.Entity.User;
import antifraud.Repositories.UserRepository;
import antifraud.Service.userService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class userServiceImpl implements userService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public userServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {

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

        return user;
    }

    @Override
    public List<User> getAllUsers() {
       return userRepository.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "id"));
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    public User changeUserRole(String name, String role) {

        if (!role.equals("SUPPORT") & !role.equals("MERCHANT")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = userRepository.findByUsernameIgnoreCase(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getRole().equals(role)) throw new ResponseStatusException(HttpStatus.CONFLICT);

        if (user.getRole().equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        user.setRole(role);
        userRepository.save(user);

        return user;
    }

    @Override
    public Map<String, String> changeUserStatus(String name, String operation) {

        User user = userRepository.findByUsernameIgnoreCase(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (user.getRole().equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        switch (operation) {
            case "LOCK":
                user.setAccountNonLocked(false);
                userRepository.save(user);
                return Map.of("status", "User " + user.getUsername() + " locked!");
            case "UNLOCK":
                user.setAccountNonLocked(true);
                userRepository.save(user);
                return Map.of("status", "User " + user.getUsername() + " unlocked!");
        }
        return Map.of();

    }
}
