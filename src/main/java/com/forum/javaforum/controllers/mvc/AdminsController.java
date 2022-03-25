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

import static com.forum.javaforum.controllers.mvc.UsersMVCController.*;

@Controller
@RequestMapping("/admins")
public class AdminsController {

    private final AdminService adminService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AdminsController(AdminService adminService, UserService userService, AuthenticationHelper authenticationHelper) {
        this.adminService = adminService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showAdminsPage(HttpSession session,
                                 Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        List<User> adminsList = adminService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("username"), Optional.empty());

        model.addAttribute("admins", adminsList);
        model.addAttribute("filterDTO", new UsersFilterDTO());

        return "admins";
    }

    @PostMapping
    public String showAdminsPageFiltered(HttpSession session,
                                         Model model,
                                         @ModelAttribute("filterDTO") UsersFilterDTO filterDTO) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        List<User> adminsList = adminService.getAll(Optional.ofNullable(filterDTO.getUsername().isEmpty() ? null : filterDTO.getUsername()),
                Optional.ofNullable(filterDTO.getFirstName().isEmpty() ? null : filterDTO.getFirstName()),
                Optional.ofNullable(filterDTO.getEmail().isEmpty() ? null : filterDTO.getEmail()),
                Optional.of(filterDTO.getSortBy()),
                Optional.of(filterDTO.getSortOrder()));

        model.addAttribute("admins", adminsList);
        model.addAttribute("filterDTO", filterDTO);

        return "admins";
    }

}
