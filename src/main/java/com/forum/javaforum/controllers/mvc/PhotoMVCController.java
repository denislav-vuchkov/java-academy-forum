package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.PhotoDTO;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.PhotoService;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.io.IOException;

import static com.forum.javaforum.controllers.mvc.UsersMVCController.*;

@Controller
@RequestMapping()
public class PhotoMVCController {

    private final PhotoService photoService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PhotoMVCController(PhotoService photoService,
                              UserService userService,
                              UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.photoService = photoService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/users/{id}/photo")
    public String showUpdatePhotoPage(HttpSession session, @PathVariable int id, Model model) {
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
            model.addAttribute("photoDTO", new PhotoDTO());

            return "user-photo-update";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @RequestMapping(path="/users/photo/upload", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadPhotoOption2(@ModelAttribute("photoDTO") PhotoDTO photoDTO,
                                     HttpSession session,
                                     Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            User user;

            if (photoDTO.getPhoto().isEmpty() || photoDTO.getPhoto() == null) {
                model.addAttribute("user", userMapper.UserToDTOOut(currentUser));
                model.addAttribute("photoDTO", new PhotoDTO());
                return "user-photo-update";
            }

            if (currentUser.getPhoto() == null) {
                user = photoService.save(photoDTO.getPhoto(), currentUser.getId());
            } else {
                user = photoService.update(photoDTO.getPhoto(), currentUser.getId());
            }

            model.addAttribute("user", userMapper.UserToDTOOut(user));

            return "redirect:/users/" + user.getId();

        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        } catch (EntityNotFoundException | IOException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    //This method was used with the script for uploading Multipart Files (it didn't want to reload the HTML I was returning)

    /*@PostMapping("/users/photo/upload")
    public String uploadPhoto(@RequestParam("file") MultipartFile file, HttpSession session, Model model) throws IOException {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            User user;

            if (currentUser.getPhoto() == null) {
                user = photoService.save(file, currentUser.getId());
            } else {
                user = photoService.update(file, currentUser.getId());
            }

            model.addAttribute("user", userMapper.UserToDTOOut(user));

            return "redirect:/users/" + user.getId();

        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }*/


    @GetMapping("/users/{id}/photo/delete")
    public String deleteUserPhoto(HttpSession session, @PathVariable int id, Model model) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        }

        try {
            User user = photoService.delete(id);

            model.addAttribute("user", userMapper.UserToDTOOut(user));
            return "redirect:/users/" + id;

        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", UNAUTHORISED_NOT_LOGGED);
            return "unauthorised";
        } catch (EntityNotFoundException | IOException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }
}
