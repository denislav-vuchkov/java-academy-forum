package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.LikeService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/posts")
public class LikeMVCController {

    private final LikeService likeService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public LikeMVCController(LikeService likeService, AuthenticationHelper authenticationHelper) {
        this.likeService = likeService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{postID}/add-like")
    public String addLike(@PathVariable int postID, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            likeService.addLike(postID, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{postID}/remove-like")
    public String removeLike(@PathVariable int postID, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            likeService.removeLike(postID, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }
}