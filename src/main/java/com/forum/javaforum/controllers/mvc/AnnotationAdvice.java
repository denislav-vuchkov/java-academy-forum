package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@ControllerAdvice("com.forum.javaforum.controllers.mvc")
public class AnnotationAdvice {

    private final UserService userService;

    public AnnotationAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("currentUser")
    public User populateIsAdmin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {

            try {
                return userService.getByUsername(session.getAttribute("currentUser").toString());
            } catch (EntityNotFoundException e) {
                return null;
            }
        }

        return null;
    }
}
