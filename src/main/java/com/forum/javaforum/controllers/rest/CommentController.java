package com.forum.javaforum.controllers.rest;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.CommentService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService service;
    private final CommentMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentController(CommentService service, CommentMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/comments")
    public List<Comment> getComments(@RequestHeader HttpHeaders headers,
                                     @RequestParam(required = false) Optional<String> author,
                                     @RequestParam(required = false) Optional<Integer> postID,
                                     @RequestParam(required = false) Optional<String> keyword,
                                     @RequestParam(required = false) Optional<String> sortBy,
                                     @RequestParam(required = false) Optional<String> order) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.getAll(author, postID, keyword, sortBy, order, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/posts/{postID}/comments")
    public Comment addComment(@RequestHeader HttpHeaders headers,
                              @PathVariable int postID,
                              @Valid @RequestBody CommentDto commentDto) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            Comment comment = mapper.fromDto(commentDto, postID, user);
            service.addComment(comment, user);
            return comment;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PatchMapping("/posts/{postID}/comments/{commentID}")
    public Comment editComment(@RequestHeader HttpHeaders headers,
                               @PathVariable int postID,
                               @PathVariable int commentID,
                               @Valid @RequestBody CommentDto commentDto) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            Comment original = service.getCommentIfExists(postID, commentID);
            Comment modified = mapper.fromDto(original, commentDto);
            return service.editComment(postID, commentID, modified, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{postID}/comments/{commentID}")
    public Comment removeComment(@RequestHeader HttpHeaders headers,
                                 @PathVariable int postID,
                                 @PathVariable int commentID) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.removeComment(postID, commentID, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/comments/count")
    public Long getNumberOfComments() {
        return service.getNumberOfComments();
    }
}
