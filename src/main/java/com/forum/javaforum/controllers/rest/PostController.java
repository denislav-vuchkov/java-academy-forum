package com.forum.javaforum.controllers.rest;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.PostDto;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;
    private final PostMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostController(PostService service, PostMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Post> getAll(@RequestHeader HttpHeaders headers,
                             @RequestParam(required = false) Optional<String> author,
                             @RequestParam(required = false) Optional<String> keyword,
                             @RequestParam(required = false) Optional<String> tag,
                             @RequestParam(required = false) Optional<String> sortBy,
                             @RequestParam(required = false) Optional<String> order,
                             @RequestParam(required = false) Optional<String> limit) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.getAll(author, keyword, tag, sortBy, order, limit, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/mostCommented")
    public List<Post> getMostCommented() {
        return service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("comments"), Optional.of("desc"), Optional.of("10"));
    }

    @GetMapping("/mostLiked")
    public List<Post> getMostLiked() {
        return service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("likes"), Optional.of("desc"), Optional.of("10"));
    }

    @GetMapping("/mostRecent")
    public List<Post> getMostRecent() {
        return service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("date"), Optional.of("desc"), Optional.of("10"));
    }

    @GetMapping("/{id}")
    public Post getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.getById(id, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Post create(@RequestHeader HttpHeaders headers,
                       @Valid @RequestBody PostDto postDto) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            Post post = mapper.fromDto(postDto, user);
            service.create(post, user);
            mapper.updateTags(post, postDto, user);
            return getById(headers, post.getId());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public Post update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody PostDto postDto) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            Post original = service.getById(id, user);
            Post modified = mapper.fromDto(original, postDto, user);
            return service.update(modified, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Post delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.delete(id, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/count")
    public Long getNumberOfPosts() {
        return service.getNumberOfPosts();
    }
}
