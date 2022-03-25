package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.CommentService;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.forum.javaforum.utilities.PublicationValidators.*;

@Controller
@RequestMapping("/posts")
public class CommentMVCController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentMVCController(CommentService commentService, CommentMapper commentMapper,
                                PostService postService, AuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
    }

    @PostMapping("/{postID}/add-comment")
    public String addComment(@PathVariable int postID, @Valid @ModelAttribute("comment") CommentDto commentDto,
                             BindingResult errors, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (errors.hasErrors()) {
                Post post = postService.getById(postID, user);
                model.addAttribute("post", post);
                model.addAttribute("comment", commentDto);
                return "post";
            }
            Comment comment = commentMapper.fromDto(commentDto, postID, user);
            commentService.addComment(comment, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException|AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{postID}/edit-comment/{commentID}")
    public String showEditCommentPage(@PathVariable int postID, @PathVariable int commentID,
                                      Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Comment comment = commentService.getCommentIfExists(postID, commentID);
            validatePublicationModification(user,comment.getAuthor().getId());
            CommentDto commentDto = commentMapper.toDto(comment);
            model.addAttribute("commentID", commentID);
            model.addAttribute("comment", commentDto);
            return "comment-update";
        } catch (UnauthorizedOperationException|AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{postID}/edit-comment/{commentID}")
    public String saveComment(@PathVariable int postID, @PathVariable int commentID,
                              @Valid @ModelAttribute("comment") CommentDto commentDto,
                              BindingResult errors, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (errors.hasErrors()) return "comment-update";
            Comment original = commentService.getCommentIfExists(postID, commentID);
            Comment modified = commentMapper.fromDto(original, commentDto);
            commentService.editComment(postID, commentID, modified, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException|AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{postID}/remove-comment/{commentID}")
    public String removeComment(@PathVariable int postID, @PathVariable int commentID,
                                Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            commentService.removeComment(postID, commentID, user);
            return "redirect:/posts/" + postID;
        } catch (UnauthorizedOperationException|AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }
}