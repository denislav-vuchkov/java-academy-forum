package com.forum.javaforum.controllers.rest;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.*;
import com.forum.javaforum.services.contracts.AdminService;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AdminController(AdminService adminService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.adminService = adminService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public List<UserDTOOutInterface> getAll(@RequestHeader HttpHeaders headers,
                                            @RequestParam(required = false) Optional<String> username,
                                            @RequestParam(required = false) Optional<String> firstName,
                                            @RequestParam(required = false) Optional<String> email,
                                            @RequestParam(required = false) Optional<String> sortBy,
                                            @RequestParam(required = false) Optional<String> sortOrder) {

        User requester = authenticationHelper.tryGetUser(headers);

        try {
            return adminService.getAll(username, firstName, email, sortBy, sortOrder)
                    .stream().map(userMapper::UserToDTOOut).collect(Collectors.toList());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public UserDTOOutInterface getByID(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);
        User adminById;

        try {
            adminById = adminService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        if (requester.isAdmin()) {
            return userMapper.UserToDTOOutOwner(adminById);
        } else {
            return userMapper.UserToDTOOut(adminById);
        }
    }


    @PutMapping("/make/{id}")
    public UserDTOOutInterface makeAdmin(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            return userMapper.UserToDTOOutOwner(adminService.makeAdmin(requester, id));
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/remove/{id}")
    public UserDTOOutInterface removeAdmin(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            return userMapper.UserToDTOOutOwner(adminService.removeAdmin(requester, id));
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/block/{id}")
    public UserDTOOutInterface blockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            return userMapper.UserToDTOOutOwner(adminService.blockUser(requester, id));
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/unblock/{id}")
    public UserDTOOutInterface unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);

        try {
            return userMapper.UserToDTOOutOwner(adminService.unblockUser(requester, id));
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
