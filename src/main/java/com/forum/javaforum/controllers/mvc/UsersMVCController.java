package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.*;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.models.users.UserDTOInImpl;
import com.forum.javaforum.models.users.UsersFilterDTO;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping()
public class UsersMVCController {

    public static final String UNAUTHORISED_NOT_LOGGED = "You have to be logged in order to access the requested resource!";
    public static final String UNAUTHORISED_NOT_OWNER = "You have to be the owner of the account in order to access the requested resource!";
    public static final String UNAUTHORISED_NOT_ADMIN = "You have to be an administrator to access the requested resource!";
    public static final String UNAUTHORISED_LOGGED = "You cannot access this resource while logged in while logged in! Please log out and try again.";
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UsersMVCController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public String showUsersPage(HttpSession session,
                                Model model) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        List<User> userList = userService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());

        model.addAttribute("users",
                userList.stream()
                        .map(userMapper::UserToDTOOut)
                        .collect(Collectors.toList()));

        model.addAttribute("filterDTO", new UsersFilterDTO());

        return "users";
    }

    @PostMapping("/users")
    public String showUsersPageFiltered(HttpSession session,
                                        Model model,
                                        @ModelAttribute("filterDTO") UsersFilterDTO filterDTO) {

        try {
            User currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        List<User> userList = userService.getAll(Optional.ofNullable(filterDTO.getUsername().isEmpty() ? null : filterDTO.getUsername()),
                Optional.ofNullable(filterDTO.getFirstName().isEmpty() ? null : filterDTO.getFirstName()),
                Optional.ofNullable(filterDTO.getEmail().isEmpty() ? null : filterDTO.getEmail()),
                Optional.of(filterDTO.getSortBy()),
                Optional.of(filterDTO.getSortOrder()));

        model.addAttribute("users",
                userList.stream()
                        .map(userMapper::UserToDTOOut)
                        .collect(Collectors.toList()));

        model.addAttribute("users", userList);
        model.addAttribute("filterDTO", filterDTO);

        return "users";
    }


    @GetMapping("/profile")
    public String showMyProfile(HttpSession session, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            model.addAttribute("user", userMapper.UserToDTOOut(currentUser));
            return "user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/users/{id}")
    public String showUserProfile(HttpSession session, @PathVariable int id, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            User user = userService.getById(id);
            model.addAttribute("user", userMapper.UserToDTOOut(user));
            return "user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/users/{id}/update")
    public String showUpdatePage(HttpSession session, @PathVariable int id, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        if (currentUser.getId() != id) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_OWNER);
            return "unauthorised";
        }

        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);

            UserDTOInImpl userDTOInImpl = userMapper.UserToDTOIn(user);
            model.addAttribute("userDTO", userDTOInImpl);

            return "user-update";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/users/{id}/update")
    public String updateProfile(HttpSession session,
                                Model model,
                                @PathVariable int id,
                                @Valid @ModelAttribute("userDTO") UserDTOInImpl userDTOInImpl,
                                BindingResult errors) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        if (errors.hasErrors()) {
            model.addAttribute("user", userService.getById(id));
            return "user-update";
        }

        try {
            User requester = userService.getByUsername(session.getAttribute("currentUser").toString());

            User user = userMapper.toObject(userDTOInImpl, id);

            userService.update(requester, id, user);

            user = userService.getById(id);

            model.addAttribute("user", user);

            return "redirect:/users/{id}";

        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "not-found";
        } catch (InvalidParameter e) {
            if (e.getMessage().contains("ID")) {
                model.addAttribute("errorMessage", e.getMessage());
                return "unauthorised";
            } else {
                model.addAttribute("user", userService.getById(id));
                errors.rejectValue("phoneNumber", "invalid_number", e.getMessage());

                return "user-update";
            }
        } catch (DuplicateEntityException e) {
            model.addAttribute("user", userService.getById(id));
            if (e.getMessage().contains("email")) {
                errors.rejectValue("email", "duplicate_email", e.getMessage());
            } else if (e.getMessage().contains("number")) {
                errors.rejectValue("phoneNumber", "duplicate_number", e.getMessage());
            }

            return "user-update";
        }
    }

    @GetMapping("/users/{id}/delete")
    public String showDeleteProfilePage(HttpSession session, @PathVariable int id, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        if (currentUser.getId() != id) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_OWNER);
            return "unauthorised";
        }

        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);
            return "delete-account";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteProfile(HttpSession session, @PathVariable int id, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            User user = userService.getById(id);
            model.addAttribute("user", user);

            userService.delete(user, id);
            session.removeAttribute("currentUser");

            return "redirect:/goodbye";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (DuplicateEntityException | InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/goodbye")
    public String showGoodbyePage() {
        return "goodbye-page";
    }

}
