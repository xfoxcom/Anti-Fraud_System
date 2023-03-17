package antifraud.controllers;

import antifraud.entity.User;
import antifraud.request.accessRequest;
import antifraud.request.roleRequest;
import antifraud.response.Response;
import antifraud.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final userService userService;

    @PostMapping("/user")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Response> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(new Response(username, "Deleted successfully!"));
    }

    @PutMapping("/role")
    public User changeRole(@RequestBody roleRequest roleRequest) {
        String username = roleRequest.getUsername();
        String role = roleRequest.getRole();

        return userService.changeUserRole(username, role);
    }

    @PutMapping("/access")
    public Map<String, String> changeStatus(@RequestBody accessRequest accessRequest) {

        String username = accessRequest.getUsername();
        String operation = accessRequest.getOperation();

        return userService.changeUserStatus(username, operation);
    }
}
