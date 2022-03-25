package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.LikeService;
import com.forum.javaforum.services.contracts.TagService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class TagMVCController {

    private final TagService tagService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TagMVCController(TagService tagService, AuthenticationHelper authenticationHelper) {
        this.tagService = tagService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{postID}/add-tag")
    public String addLike(@PathVariable int postID, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
   //         tagService.addTag(postID, tag, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{postID}/remove-tag")
    public String removeLike(@PathVariable int postID, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
    //        tagService.removeTag(postID,tag,user);
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