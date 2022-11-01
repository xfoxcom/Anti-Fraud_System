package antifraud.Service;

import antifraud.Entity.User;

import java.util.List;
import java.util.Map;

public interface userService {

    User register(User user);

    List<User> getAllUsers();

    void deleteUser(String username);

    User changeUserRole(String name, String role);

    Map<String, String> changeUserStatus(String name, String operation);

}
