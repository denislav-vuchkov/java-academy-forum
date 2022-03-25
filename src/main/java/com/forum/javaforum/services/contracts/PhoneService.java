package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.models.users.User;

public interface PhoneService {

    void update(User updatedUser, User originalUser);

    void save(PhoneNumber phoneNumber);

    void delete(PhoneNumber phoneNumber);

}
