package com.forum.javaforum.controllers.rest;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.LikeService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService service;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public LikeController(LikeService service, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{postID}/likes")
    public List<User> getLikes(@RequestHeader HttpHeaders headers, @PathVariable int postID) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.getLikes(postID, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{postID}/likes")
    public User addLike(@RequestHeader HttpHeaders headers, @PathVariable int postID) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.addLike(postID, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{postID}/likes")
    public User removeLike(@RequestHeader HttpHeaders headers, @PathVariable int postID) {
        User user = authenticationHelper.tryGetUser(headers);
        try {
            return service.removeLike(postID, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}