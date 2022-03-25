package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.*;
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

import static com.forum.javaforum.controllers.mvc.UsersMVCController.UNAUTHORISED_LOGGED;
import static com.forum.javaforum.controllers.mvc.UsersMVCController.UNAUTHORISED_NOT_LOGGED;

@Controller
@RequestMapping("/auth")
public class AuthenticationMVC {

    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Autowired
    public AuthenticationMVC(AuthenticationHelper authenticationHelper,
                             UserMapper userMapper,
                             UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        if (authenticationHelper.isUserLoggedIn(session)) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        model.addAttribute("loginDTO", new LoginDTO());

        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                               BindingResult bindingResult,
                               HttpSession httpSession) {
        try {
            User user = authenticationHelper.verifyAuthentication(loginDTO.getUsername(), loginDTO.getPassword());

            httpSession.setAttribute("currentUser", loginDTO.getUsername());

            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "login";
        }

    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session, Model model) {
        if (!authenticationHelper.isUserLoggedIn(session)) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        session.removeAttribute("currentUser");

        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationPage(HttpSession session, Model model) {
        if (authenticationHelper.isUserLoggedIn(session)) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        RegisterDTO registerDTO = new RegisterDTO();

        model.addAttribute("registerDTO", registerDTO);

        return "register";
    }


    @PostMapping("/register")
    public String registerUser(Model model,
                               @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
                               BindingResult errors) {

        if (errors.hasErrors()) {
            return "register";
        }

        if (!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password!");
            return "register";
        }

        try {
            User user = userMapper.toObject(registerDTO);

            userService.create(user);

            return "redirect:/auth/success";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "unauthorised";
        } catch (DuplicateEntityException e) {
            if (e.getMessage().contains("username")) {
                errors.rejectValue("username", "duplicate_username", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                errors.rejectValue("email", "duplicate_email" ,e.getMessage());
            }

            return "register";
        }
    }

    @GetMapping("/success")
    public String showRegistrationSuccess() {
        return "registration-success";
    }

    @GetMapping("/confirm-token")
    public String confirmToken(@RequestParam String token, Model model) {
        try {
            userService.confirmToken(token);

            return "registration-token-success";
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "registration-token-failure";
        }
    }

    @GetMapping("/generate-token")
    public String generateNewToken(@RequestParam(name="username") String username, Model model) {
        try {
            User user = userService.getByUsername(username);

            userService.generateConfirmationToken(user);

            return "verify-email";
        } catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/request-token")
    public String generateNewToken() {
        return "generate-token";
    }

}
