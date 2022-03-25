package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.AdminRepository;
import com.forum.javaforum.repositories.contracts.PhoneRepository;
import com.forum.javaforum.services.contracts.AdminService;
import com.forum.javaforum.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    public static final String UNAUTHORIZED = "You do not have sufficient rights to perform this operation.";
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final PhoneRepository phoneRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, UserService userService, PhoneRepository phoneRepository) {
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public List<User> getAll(Optional<String> username, Optional<String> firstName,
                             Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder) {
        return adminRepository.getAll(username, firstName, email, sortBy, sortOrder);
    }

    @Override
    public List<User> getAllNonAdmins(String usernameFilter) {
        return adminRepository.getAllNonAdmins(usernameFilter);
    }

    @Override
    public List<User> getAllBlockedUsers(String usernameFilter) {
        return adminRepository.getAllBlockedUsers(usernameFilter);
    }

    @Override
    public List<User> getAllUnblockedUsers(String usernameFilter) {
        return adminRepository.getAllUnblockedUsers(usernameFilter);
    }

    @Override
    public List<User> getAllNonDeletedUsers(String usernameFilter) {
        return adminRepository.getAllNonDeletedUsers(usernameFilter);
    }

    @Override
    public User getById(int id) {
        validateID(id);

        return adminRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return adminRepository.getByUsername(username);
    }

    @Override
    public User makeAdmin(User requester, int id) {
        validateID(id);
        validateIfAdmin(requester);
        User targetUser = userService.getById(id);
        validateIfDeleted(targetUser);
        validateIfBlocked(targetUser);

        if (targetUser.isAdmin()) {
            throw new DuplicateEntityException(String.format("User with id %d is already an admin.", id));
        }

        return adminRepository.makeAdmin(targetUser);
    }

    @Override
    public User removeAdmin(User requester, int id) {
        validateID(id);
        validateIfAdmin(requester);
        User targetUser = userService.getById(id);
        validateIfDeleted(targetUser);

        if (!targetUser.isAdmin()) {
            throw new InvalidParameter(String.format("User with id %d is not an admin.", id));
        }

        if (targetUser.getPhoneNumber() != null) {
            phoneRepository.delete(targetUser.getPhoneNumber());
        }

        return adminRepository.removeAdmin(targetUser);
    }

    @Override
    public User blockUser(User requester, int id) {
        validateID(id);
        validateIfAdmin(requester);
        User targetUser = userService.getById(id);
        validateIfDeleted(targetUser);

        if (targetUser.isBlocked()) {
            throw new DuplicateEntityException(String.format("User with id %d is already blocked.", id));
        }

        return adminRepository.blockUser(targetUser);
    }

    @Override
    public User unblockUser(User requester, int id) {
        validateID(id);
        validateIfAdmin(requester);
        User targetUser = userService.getById(id);
        validateIfDeleted(targetUser);

        if (!targetUser.isBlocked()) {
            throw new InvalidParameter(String.format("User with id %d is not blocked.", id));
        }

        return adminRepository.unblockUser(targetUser);
    }

    private void validateIfDeleted(User targetUser) {
        if (targetUser.isDeleted()) {
            throw new UnauthorizedOperationException(String.format("User with username %s is deleted and this operation" +
                    " cannot be applied to a deleted user.", targetUser.getUsername()));
        }
    }


    private void validateIfBlocked(User targetUser) {
        if (targetUser.isBlocked()) {
            throw new UnauthorizedOperationException(String.format("User with username %s is blocked and cannot " +
                    "be made admin while they are blocked in the system. Please unblock before promoting.", targetUser.getUsername()));
        }
    }

    private void validateIfAdmin(User requester) {
        if (!requester.isAdmin()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED);
        }
    }

    private void validateID(int id) {
        if (id <= 0) {
            throw new InvalidParameter("Please provide a positive integer as ID.");
        }
    }

}
