package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface AdminRepository {
    List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder);

    User getById(int id);

    User getByUsername(String username);

    User makeAdmin(User targetUser);

    User removeAdmin(User targetUser);

    User blockUser(User targetUser);

    User unblockUser(User targetUser);

    List<User> getAllNonAdmins(String usernameFilter);

    List<User> getAllBlockedUsers(String usernameFilter);

    List<User> getAllUnblockedUsers(String usernameFilter);

    List<User> getAllNonDeletedUsers(String usernameFilter);
}
