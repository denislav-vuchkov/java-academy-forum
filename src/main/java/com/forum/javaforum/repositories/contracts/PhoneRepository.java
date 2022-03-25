package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.PhoneNumber;

public interface PhoneRepository {

    void save(PhoneNumber number);

    void update(PhoneNumber number);

    void delete(PhoneNumber number);

    void validatePhoneIsUnique(String value);

}
