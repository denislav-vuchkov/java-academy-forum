package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder);

    User getById(int id);

    User getByUsername(String username);

    User makeAdmin(User requester, int id);

    User removeAdmin(User requester, int userID);

    User blockUser(User requester, int id);

    User unblockUser(User requester, int id);

    List<User> getAllNonAdmins(String usernameFilter);

    List<User> getAllBlockedUsers(String usernameFilter);

    List<User> getAllUnblockedUsers(String usernameFilter);

    List<User> getAllNonDeletedUsers(String usernameFilter);
}
