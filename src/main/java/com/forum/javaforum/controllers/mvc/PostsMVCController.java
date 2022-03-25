package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.exceptions.AuthenticationFailureException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.PostDto;
import com.forum.javaforum.models.publication.PostFilterDto;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

import static com.forum.javaforum.utilities.PublicationValidators.validatePublicationCreation;
import static com.forum.javaforum.utilities.PublicationValidators.validatePublicationModification;

@Controller
@RequestMapping("/posts")
public class PostsMVCController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostsMVCController(PostService postService, PostMapper postMapper,
                              AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public String showAllPosts(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("postFilterDto", new PostFilterDto());
            model.addAttribute("posts", postService.getAll(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of("date"),
                    Optional.of("desc"),
                    Optional.empty(), user));

            return "posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping()
    public String showAllPosts(@ModelAttribute("postFilterDto") PostFilterDto postFilterDto,
                               Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("posts", postService.getAll(
                    Optional.ofNullable(postFilterDto.getAuthor().isEmpty() ? null : postFilterDto.getAuthor()),
                    Optional.ofNullable(postFilterDto.getKeyword().isEmpty() ? null : postFilterDto.getKeyword()),
                    Optional.ofNullable(postFilterDto.getTag().isEmpty() ? null : postFilterDto.getTag()),
                    Optional.ofNullable(postFilterDto.getSortBy().isEmpty() ? "date" : postFilterDto.getSortBy()),
                    Optional.ofNullable(postFilterDto.getOrder().isEmpty() ? "desc" : postFilterDto.getOrder()),
                    Optional.empty(), user));

            model.addAttribute("postFilterDto", postFilterDto);
            return "posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Post post = postService.getById(id, user);
            model.addAttribute("post", post);
            model.addAttribute("comment", new CommentDto());
            return "post";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/new")
    public String showNewPostPage(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            validatePublicationCreation(user);
            model.addAttribute("post", new PostDto());
            return "post-new";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("post") PostDto postDto,
                         BindingResult errors, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (errors.hasErrors()) return "post-new";
            Post post = postMapper.fromDto(postDto, user);
            postService.create(post, user);
            postMapper.updateTags(post, postDto, user);
            return "redirect:/posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditPostPage(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Post post = postService.getById(id, user);
            validatePublicationModification(user, post.getAuthor().getId());
            PostDto postDto = postMapper.toDto(post);
            model.addAttribute("post", postDto);
            return "post-update";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id,
                         @Valid @ModelAttribute("post") PostDto postDto,
                         BindingResult errors, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (errors.hasErrors()) return "post-update";
            Post original = postService.getById(id, user);
            Post modified = postMapper.fromDto(original, postDto, user);
            postService.update(modified, user);
            return "redirect:/posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            postService.delete(id, user);
            return "redirect:/posts";
        } catch (UnauthorizedOperationException | AuthenticationFailureException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }
}