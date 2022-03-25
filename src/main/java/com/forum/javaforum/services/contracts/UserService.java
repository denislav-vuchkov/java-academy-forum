package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.LoginDTO;
import com.forum.javaforum.models.users.User;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder);

    User getById(int id);

    User getByUsername(String username);

    User create(HttpHeaders headers, User user);

    User create(User user);

    User update(User requester, int id, User user);

    User delete(User requester, int id);

    Long getNumberOfUsers();

    Long getNumberOfInactiveUsers();

    Long getNumberOfAdmins();

    void enableUser(int id);

    String confirmToken(String token);

    String generateConfirmationToken(User user);
}
