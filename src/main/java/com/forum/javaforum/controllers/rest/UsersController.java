package com.forum.javaforum.controllers.rest;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.models.users.UserDTOInImpl;
import com.forum.javaforum.models.users.UserDTOOutInterface;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService service;
    private final UserMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UsersController(UserService service, UserMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }


    @GetMapping()
    public List<UserDTOOutInterface> getAllUsers(@RequestHeader HttpHeaders headers,
                                                 @RequestParam(required = false) Optional<String> username,
                                                 @RequestParam(required = false) Optional<String> firstName,
                                                 @RequestParam(required = false) Optional<String> email,
                                                 @RequestParam(required = false) Optional<String> sortBy,
                                                 @RequestParam(required = false) Optional<String> sortOrder) {

        List<User> result;
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            result = service.getAll(username, firstName, email, sortBy, sortOrder);
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return result.stream().map(mapper::UserToDTOOut).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDTOOutInterface getUserByID(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User userById;
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            userById = service.getById(id);
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        if (requester.getId() == id) {
            return mapper.UserToDTOOutOwner(userById);
        } else {
            return mapper.UserToDTOOut(userById);
        }
    }

    @PostMapping()
    public UserDTOOutInterface createUser(@RequestHeader HttpHeaders headers,
                                          @Valid @RequestBody UserDTOInImpl userDTOInImpl) {
        User userObject = mapper.toObject(userDTOInImpl);
        try {
            userObject = service.create(headers, userObject);
        }  catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return mapper.UserToDTOOut(userObject);
    }

    @GetMapping("/confirm-token")
    public String confirmToken(@RequestParam String token) {
        try {
            return service.confirmToken(token);
        } catch (IllegalStateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public UserDTOOutInterface updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id,
                                          @Valid @RequestBody UserDTOInImpl userDTOInImpl) {

        User requester = authenticationHelper.tryGetUser(headers);
        User userObject = mapper.toObject(userDTOInImpl, id);

        try {
            userObject = service.update(requester, id, userObject);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }  catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }  catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return mapper.UserToDTOOut(userObject);
    }
    
    @DeleteMapping("/{id}")
    public UserDTOOutInterface deleteUser(@RequestHeader HttpHeaders headers,  @PathVariable int id) {
        User deletedUser;
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            deletedUser = service.delete(requester, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return mapper.UserToDTOOutOwner(deletedUser);
    }

    @GetMapping("/count")
    public Long getNumberOfUsers() {
        return service.getNumberOfUsers();
    }

    @GetMapping("/countinactive")
    public Long getNumberOfInactiveUsers() {
        return service.getNumberOfInactiveUsers();
    }

    @GetMapping("/countadmins")
    public Long getNumberOfAdmins() {
        return service.getNumberOfAdmins();
    }

}
