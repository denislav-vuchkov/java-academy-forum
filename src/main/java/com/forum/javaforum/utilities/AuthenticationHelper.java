package com.forum.javaforum.utilities;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

import static com.forum.javaforum.controllers.mvc.UsersMVCController.UNAUTHORISED_NOT_LOGGED;

@Component
public class AuthenticationHelper {

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The requested resource requires authentication.");
        }
        try {
            String username = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            return userService.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username.");
        }
    }

    public User tryGetUser(HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");

        if (currentUser == null) {
            throw new AuthenticationFailureException(UNAUTHORISED_NOT_LOGGED);
        }

        return userService.getByUsername(currentUser);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByUsername(username);

            if (!user.isEnabled()) {
                throw new AuthenticationFailureException("This account has not been activated yet. Please check your email for the verification email.");
            }

            if (user.isDeleted()) {
                throw new AuthenticationFailureException("This account has been deleted and no longer active.");
            }

            if (!user.getPassword().equals(password)) {
                throw new EntityNotFoundException();
            }

            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException("Wrong username or password");
        }
    }

    public boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

}

