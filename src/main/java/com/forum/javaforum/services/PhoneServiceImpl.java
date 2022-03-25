package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PhoneRepository;
import com.forum.javaforum.services.contracts.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneServiceImpl implements PhoneService {

    private final PhoneRepository repository;

    @Autowired
    public PhoneServiceImpl(PhoneRepository repository) {
        this.repository = repository;
    }

    @Override
    public void update(User updatedUser, User originalUser) {
        if (updatedUser.getPhoneNumber().getNumber() != null) {
            validatePhoneNumber(updatedUser.getPhoneNumber().getNumber());

            if (originalUser.getPhoneNumber() == null) {
                repository.validatePhoneIsUnique(updatedUser.getPhoneNumber().getNumber());
                repository.save(updatedUser.getPhoneNumber());
            } else {
                if (checkIfNewNumberSameAsOld(updatedUser, originalUser)) return;
                repository.validatePhoneIsUnique(updatedUser.getPhoneNumber().getNumber());
                repository.update(updatedUser.getPhoneNumber());
            }
        } else if (originalUser.getPhoneNumber() != null) {
            repository.delete(originalUser.getPhoneNumber());
        }
    }

    @Override
    public void save(PhoneNumber phoneNumber) {
        repository.save(phoneNumber);
    }

    @Override
    public void delete(PhoneNumber phoneNumber) {
        repository.delete(phoneNumber);
    }

    private boolean checkIfNewNumberSameAsOld(User updatedUser, User originalUser) {
        return updatedUser.getPhoneNumber().getNumber().equals(originalUser.getPhoneNumber().getNumber());
    }

    private void validatePhoneNumber(String number) {
        String regex = "^\\+?[0-9]{9,14}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);

        if (!matcher.find()) {
            throw new InvalidParameter("Please provide a valid phone number.");
        }
    }

}
