package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.*;
import com.forum.javaforum.models.users.User;
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
@RequestMapping("/admins/v1/modify")
public class ModifyRightsOneMVC {

    public static final String EMPTY_STRING = "";
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final AdminService adminService;

    @Autowired
    public ModifyRightsOneMVC(AuthenticationHelper authenticationHelper, UserService userService, AdminService adminService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping()
    public String showModifyRightsPage(HttpSession session, Model model) {
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

        return "modify-rights";
    }

    @ModelAttribute("users")
    public List<User> getAllUsers() {
        return userService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
    }


    @ModelAttribute("admins")
    public List<User> getAllAdmins() {
        return adminService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
    }

    @ModelAttribute("nonAdmins")
    public List<User> getAllNonAdmins() {
        return adminService.getAllNonAdmins(EMPTY_STRING);
    }

    @ModelAttribute("blockedUsers")
    public List<User> getAllBlockedUsers() {
        return adminService.getAllBlockedUsers(EMPTY_STRING);
    }

    @ModelAttribute("unblockedUsers")
    public List<User> getAllUnblockedUsers() {
        return adminService.getAllUnblockedUsers(EMPTY_STRING);
    }

    @ModelAttribute("nonDeletedUsers")
    public List<User> getAllNonDeletedUsers() {
        return adminService.getAllNonDeletedUsers(EMPTY_STRING);
    }


    @PostMapping("/grant-rights")
    public String makeAdminOption2(HttpSession session,
                                   @RequestParam(name = "makeAdminID", required = false) int makeAdminID,
                                   Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.makeAdmin(requester, makeAdminID);

            return "redirect:/admins/v1/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }

    }

    @PostMapping("/remove-rights")
    public String removeAdminOption2(HttpSession session,
                                     @RequestParam(name = "removeAdminID", required = false) int removeAdminID,
                                     Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.removeAdmin(requester, removeAdminID);

            return "redirect:/admins/v1/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/block")
    public String blockUserOption2(HttpSession session,
                                   @RequestParam(name = "blockID", required = false) int blockID,
                                   Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.blockUser(requester, blockID);

            return "redirect:/admins/v1/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/unblock")
    public String unblockUser(@RequestParam(name = "unblockID", required = false) int unblockID,
                              HttpSession session,
                              Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            adminService.unblockUser(requester, unblockID);

            return "redirect:/admins/v1/modify";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam(name = "deleteID", required = false) int deleteID,
                             HttpSession session,
                             Model model) {
        User requester;
        try {
            requester = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            userService.delete(requester, deleteID);

            return "redirect:/admins/v1/modify";
        } catch (InvalidParameter | EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

}
