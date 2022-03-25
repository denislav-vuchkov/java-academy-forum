package com.forum.javaforum.utilities.mappers;

import com.forum.javaforum.models.users.*;
import com.forum.javaforum.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    private final UserRepository repository;

    @Autowired
    public UserMapper(UserRepository repository) {
        this.repository = repository;
    }

    public User toObject(UserDTOIn userDTOIn) {
        User user = new User();

        user.setUsername(userDTOIn.getUsername());
        user.setFirstName(userDTOIn.getFirstName());
        user.setLastName(userDTOIn.getLastName());
        user.setPassword(userDTOIn.getPassword());
        user.setEmail(userDTOIn.getEmail());
        user.setRegistrationTime(LocalDateTime.now());
        user.setPhoneNumber(new PhoneNumber(userDTOIn.getPhoneNumber()));

        return user;
    }

    public User toObject(UserDTOInImpl userDTOInImpl, int id) {
        User user = repository.getByID(id);

        userDTOInImpl.setUsername(user.getUsername());
        user.setFirstName(userDTOInImpl.getFirstName());
        user.setLastName(userDTOInImpl.getLastName());
        user.setPassword(userDTOInImpl.getPassword());
        user.setEmail(userDTOInImpl.getEmail());
        user.setPhoneNumber(new PhoneNumber(id, userDTOInImpl.getPhoneNumber()));

        if (userDTOInImpl.getPhoneNumber() == null || userDTOInImpl.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(new PhoneNumber(id, null));
        } else {
            user.setPhoneNumber(new PhoneNumber(id, userDTOInImpl.getPhoneNumber()));

        }

        return user;
    }

    public UserDTOInImpl UserToDTOIn(User user) {
        UserDTOInImpl userDTOInImpl = new UserDTOInImpl();

        userDTOInImpl.setUsername(user.getUsername());
        userDTOInImpl.setFirstName(user.getFirstName());
        userDTOInImpl.setLastName(user.getLastName());
        userDTOInImpl.setPassword(user.getPassword());
        userDTOInImpl.setEmail(user.getEmail());

        if (user.getPhoneNumber() != null) userDTOInImpl.setPhoneNumber(user.getPhoneNumber().getNumber());

        return userDTOInImpl;
    }

    public UserDTOOut UserToDTOOut(User user) {
        return new UserDTOOut(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getRegistrationTime(),
                user.getPhoto(), user.isAdmin(), user.isBlocked(), user.isDeleted(),
                user.getPhoneNumber(), user.isEnabled());
    }

    public UserDTOOutOwner UserToDTOOutOwner(User user) {
        return new UserDTOOutOwner(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getEmail(), user.getRegistrationTime(),
                user.getPhoto(), user.isAdmin(), user.isBlocked(), user.isDeleted(),
                user.getPhoneNumber(), user.isEnabled());
    }

}
