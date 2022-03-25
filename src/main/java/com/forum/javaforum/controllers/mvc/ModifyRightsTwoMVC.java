package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.*;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.models.users.UsersFilterDTO;
import com.forum.javaforum.services.contracts.AdminService;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.controllers.mvc.UsersMVCController.UNAUTHORISED_NOT_ADMIN;
import static com.forum.javaforum.controllers.mvc.UsersMVCController.UNAUTHORISED_NOT_LOGGED;

@Controller
@RequestMapping("admins/v2/modify")
public class ModifyRightsTwoMVC {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final AdminService adminService;

    @Autowired
    public ModifyRightsTwoMVC(AuthenticationHelper authenticationHelper, UserService userService, AdminService adminService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping()
    public String showModifyRightsPageTwo(HttpSession session, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        if (!currentUser.isAdmin()) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_ADMIN);
            return "unauthorised";
        }

        List<User> userList = userService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());

        model.addAttribute("users", userList);
        model.addAttribute("filterDTO", new UsersFilterDTO());

        return "modify-rights-option2";
    }

    @PostMapping()
    public String showModifyRightsFiltered(@ModelAttribute("filterDTO") UsersFilterDTO filterDTO,
                                           HttpSession session,
                                           Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        List<User> userList = userService.getAll(Optional.ofNullable(filterDTO.getUsername().isEmpty() ? null : filterDTO.getUsername()),
                Optional.ofNullable(filterDTO.getFirstName().isEmpty() ? null : filterDTO.getFirstName()),
                Optional.ofNullable(filterDTO.getEmail().isEmpty() ? null : filterDTO.getEmail()),
                Optional.of(filterDTO.getSortBy()),
                Optional.of(filterDTO.getSortOrder()));

        model.addAttribute("users", userList);
        model.addAttribute("filterDTO", filterDTO);

        return "modify-rights-option2";
    }

    @PostMapping("/{id}/grant-rights")
    public String makeAdmin(HttpSession session, @PathVariable int id, Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.makeAdmin(requester, id);

            model.addAttribute("users", userService.getAll(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));

            return "redirect:/admins/v2/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }

    }

    @PostMapping("/{id}/remove-rights")
    public String removeAdmin(HttpSession session, @PathVariable int id, Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.removeAdmin(requester, id);

            model.addAttribute("users", userService.getAll(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));

            return "redirect:/admins/v2/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/{id}/block")
    public String blockUser(HttpSession session, @PathVariable int id, Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.blockUser(requester, id);

            model.addAttribute("users", userService.getAll(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));

            return "redirect:/admins/v2/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/{id}/unblock")
    public String unblockUser(HttpSession session, @PathVariable int id, Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.unblockUser(requester, id);

            model.addAttribute("users", userService.getAll(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));

            return "redirect:/admins/v2/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(HttpSession session, @PathVariable int id, Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            userService.delete(requester, id);

            model.addAttribute("users", userService.getAll(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));

            return "redirect:/admins/v2/modify";
        } catch (InvalidParameter | EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

}
