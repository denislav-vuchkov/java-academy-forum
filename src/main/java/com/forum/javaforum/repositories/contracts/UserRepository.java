package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.LoginDTO;
import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder);

    User getByID(int id);

    User getByUsername(String username);

    User save(User user);

    void validateUserParamIsUnique(String column, String value);

    User update(int id, User user);

    User delete(User User);

    Long getNumberOfUsers();

    Long getNumberOfInactiveUsers();

    Long getNumberOfAdmins();

}
