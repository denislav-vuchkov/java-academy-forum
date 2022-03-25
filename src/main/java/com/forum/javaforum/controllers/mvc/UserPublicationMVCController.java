package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.*;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.CommentService;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.CommentMapper;
import com.forum.javaforum.utilities.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class UserPublicationMVCController {

    private final PostService postService;
    private final PostMapper postMapper;

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserPublicationMVCController(PostService postService, PostMapper postMapper, CommentService commentService,
                                        CommentMapper commentMapper, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/user/{username}/posts")
    public String showUserPosts(@PathVariable String username, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("titleText", username);
            model.addAttribute("postFilterDto", new PostFilterDto());
            model.addAttribute("posts", postService.getAll(
                    Optional.of(username),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("date"),
                    Optional.of("desc"),
                    Optional.empty(), user));
            return "user-posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/user/{username}/posts")
    public String showUserPosts(@PathVariable String username,
                                @ModelAttribute("postFilterDto") PostFilterDto postFilterDto,
                                Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("titleText", username);
            model.addAttribute("postFilterDto", postFilterDto);
            model.addAttribute("posts", postService.getAll(
                    Optional.of(username),
                    Optional.ofNullable(postFilterDto.getKeyword().isEmpty() ? null : postFilterDto.getKeyword()),
                    Optional.ofNullable(postFilterDto.getTag().isEmpty() ? null : postFilterDto.getTag()),
                    Optional.ofNullable(postFilterDto.getSortBy().isEmpty() ? "date" : postFilterDto.getSortBy()),
                    Optional.ofNullable(postFilterDto.getOrder().isEmpty() ? "desc" : postFilterDto.getOrder()),
                    Optional.empty(), user));
            return "user-posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/user/{username}/comments")
    public String showUserComments(@PathVariable String username, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("titleText", username);
            model.addAttribute("commentFilterDto", new CommentFilterDto());
            model.addAttribute("comments", commentService.getAll(
                    Optional.of(username),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("date"),
                    Optional.of("desc"),
                    user));
            return "user-comments";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/user/{username}/comments")
    public String showUserComments(@PathVariable String username,
                                   @ModelAttribute("commentFilterDto") CommentFilterDto commentFilterDto,
                                   Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("titleText", username);
            model.addAttribute("commentFilterDto", commentFilterDto);
            model.addAttribute("comments", commentService.getAll(
                            Optional.of(username),
                            Optional.empty(),
                            Optional.ofNullable(commentFilterDto.getKeyword().isEmpty() ? null : commentFilterDto.getKeyword()),
                            Optional.ofNullable(commentFilterDto.getSortBy().isEmpty() ? "date" : commentFilterDto.getSortBy()),
                            Optional.ofNullable(commentFilterDto.getOrder().isEmpty() ? "desc" : commentFilterDto.getOrder()),
                            user));
            return "user-comments";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }
}
