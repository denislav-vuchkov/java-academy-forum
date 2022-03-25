package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PhoneRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.forum.javaforum.HelperForTisho.createUserAdmin;

@ExtendWith(MockitoExtension.class)
public class PhoneServiceImplTests {

    @Mock
    PhoneRepository mockPhoneRepo;
    @InjectMocks
    PhoneServiceImpl service;

    @Test
    void update_should_throw_when_invalidNumberProvided() {
        User original = createUserAdmin();
        original.setPhoneNumber(new PhoneNumber("0888111111"));
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber("123"));
        Assertions.assertThrows(InvalidParameter.class, () -> service.update(modified, original));
        modified.setPhoneNumber(new PhoneNumber("123456789123456789"));
        Assertions.assertThrows(InvalidParameter.class, () -> service.update(modified, original));
        modified.setPhoneNumber(new PhoneNumber(" "));
        Assertions.assertThrows(InvalidParameter.class, () -> service.update(modified, original));
        modified.setPhoneNumber(new PhoneNumber("123ABC456"));
        Assertions.assertThrows(InvalidParameter.class, () -> service.update(modified, original));
    }

    @Test
    void update_should_checkRepoForDuplicate_when_numberIsValid() {
        User original = createUserAdmin();
        original.setPhoneNumber(new PhoneNumber("0888111111"));
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber("0888222222"));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).validatePhoneIsUnique(modified.getPhoneNumber().getNumber());
    }

    @Test
    void update_should_callRepoSaveWithNew_when_addingPhoneForFirstTime() {
        User original = createUserAdmin();
        original.setPhoneNumber(null);
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber("0888123456"));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).save(modified.getPhoneNumber());
    }

    @Test
    void update_should_checkIfNumberIsDifferent_when_changingPhoneToNew() {
        User original = createUserAdmin();
        original.setPhoneNumber(new PhoneNumber("0888111111"));
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber("0888111111"));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).update(modified.getPhoneNumber());
    }

    @Test
    void update_should_callRepoUpdateWithNew_when_changingPhoneToNew() {
        User original = createUserAdmin();
        original.setPhoneNumber(new PhoneNumber("0888111111"));
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber("0888222222"));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).update(modified.getPhoneNumber());
    }

    @Test
    void update_should_callRepoDeleteWithOld_when_changingPhoneToNothing() {
        User original = createUserAdmin();
        original.setPhoneNumber(new PhoneNumber("0888123456"));
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber(null));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).delete(original.getPhoneNumber());
    }

    @Test
    void update_should_doNothing_when_noPhoneProvided() {
        // Users w/o number are returned with null Phone object from the Repo.
        User original = createUserAdmin();
        original.setPhoneNumber(null);
        // Users w/o number are sent with a valid Phone object but null value inside by the Controller.
        User modified = createUserAdmin();
        modified.setPhoneNumber(new PhoneNumber(null));
        service.update(modified, original);
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).save(modified.getPhoneNumber());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).save(original.getPhoneNumber());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).update(modified.getPhoneNumber());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).update(original.getPhoneNumber());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).delete(modified.getPhoneNumber());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).delete(original.getPhoneNumber());
    }

    @Test
    void save_should_callRepoSave() {
        PhoneNumber phoneNumber = new PhoneNumber("0888123456");
        service.save(phoneNumber);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).save(phoneNumber);
    }

    @Test
    void delete_should_callRepoSave() {
        PhoneNumber phoneNumber = new PhoneNumber("0888123456");
        service.delete(phoneNumber);
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).delete(phoneNumber);
    }
}
