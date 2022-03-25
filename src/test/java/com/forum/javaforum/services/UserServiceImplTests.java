package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.LoginDTO;
import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.UserRepository;
import com.forum.javaforum.services.contracts.PhoneService;
import com.forum.javaforum.services.contracts.PhotoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository mockUserRepo;
    @Mock
    PhoneService mockPhoneService;
    @Mock
    PhotoService mockPhotoService;
    @InjectMocks
    UserServiceImpl service;

    @Test
    void getAll_should_callRepository() {

        List<User> mockList = List.of(createUserNormal(), createUserAdmin(), createBlockedUser(), createDeletedUser());

        Mockito.when(mockUserRepo.getAll(Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"), Optional.of("registration_date"), Optional.of("desc"))).thenReturn(mockList);

        List<User> result = service.getAll(Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"), Optional.of("registration_date"), Optional.of("desc"));

        Mockito.verify(mockUserRepo, Mockito.times(1)).getAll(Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"), Optional.of("registration_date"), Optional.of("desc"));

        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getById_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class, () -> service.getById(-1));
    }

    @Test
    void getById_should_returnUserFromRepo_when_matchExists() {
        User mockUser = createUserNormal();
        Mockito.when(mockUserRepo.getByID(mockUser.getId())).thenReturn(mockUser);
        User result = service.getById(mockUser.getId());
        Mockito.verify(mockUserRepo, Mockito.times(1)).getByID(mockUser.getId());
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void getByUsername_should_returnUserFromRepo_when_matchExists() {
        User mockUser = createUserNormal();
        Mockito.when(mockUserRepo.getByUsername(mockUser.getUsername())).thenReturn(mockUser);
        User result = service.getByUsername(mockUser.getUsername());
        Mockito.verify(mockUserRepo, Mockito.times(1)).getByUsername(mockUser.getUsername());
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void create_should_throw_when_userAlreadyRegistered() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "test");
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> service.create(headers, createUserNormal()));
    }

    @Test
    void create_should_checkRepoForDuplicate_paramUsername() {
        User mockUser = createUserNormal();
        mockUser.setPhoto(new Photo(null));
        service.create(new HttpHeaders(), mockUser);
        Mockito.verify(mockUserRepo, Mockito.times(1)).validateUserParamIsUnique("username", mockUser.getUsername());
    }

    @Test
    void create_should_checkRepoForDuplicate_paramEmail() {
        User mockUser = createUserNormal();
        mockUser.setPhoto(new Photo(null));
        service.create(new HttpHeaders(), mockUser);
        Mockito.verify(mockUserRepo, Mockito.times(1)).validateUserParamIsUnique("email", mockUser.getEmail());
    }

    @Test
    void create_should_returnUserFromRepo_when_paramsUnique() {
        User mockUser = createUserNormal();
        mockUser.setPhoto(new Photo(null));
        Mockito.when(mockUserRepo.save(mockUser)).thenReturn(mockUser);
        Mockito.when(mockUserRepo.getByUsername(mockUser.getUsername())).thenReturn(mockUser);
        User result = service.create(new HttpHeaders(), mockUser);
        Mockito.verify(mockUserRepo, Mockito.times(1)).save(mockUser);
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void create_should_savePhoto_when_paramsPhotoProvided() {
        User mockUser = createUserNormal();
        Photo photo = new Photo("test.jpg");
        mockUser.setPhoto(photo);
        Mockito.when(mockUserRepo.save(mockUser)).thenReturn(mockUser);
        Mockito.when(mockUserRepo.getByUsername(mockUser.getUsername())).thenReturn(mockUser);
        User result = service.create(new HttpHeaders(), mockUser);
        Mockito.verify(mockUserRepo, Mockito.times(1)).save(mockUser);
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void update_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class, () -> service.update(createUserNormal(), -1, createUserNormal()));
    }

    @Test
    void update_should_throw_when_requesterNotOwner() {
        User requester = createUserNormal();
        requester.setId(2);
        requester.setUsername("requester");
        User owner = createUserNormal();
        requester.setId(3);
        requester.setUsername("owner");
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> service.update(requester, owner.getId(), owner));
    }

    @Test
    void update_should_checkRepoForDuplicate_when_emailModified() {
        User original = createUserNormal();
        original.setEmail("original@mail.com");
        User modified = createUserNormal();
        modified.setEmail("modified@mail.com");
        modified.setPhoneNumber(new PhoneNumber(null));
        Mockito.when(mockUserRepo.getByID(original.getId())).thenReturn(original);
        service.update(original, original.getId(), modified);
        Mockito.verify(mockUserRepo, Mockito.times(1)).validateUserParamIsUnique("email", modified.getEmail());
    }

    @Test
    void update_should_callPhoneService_onlyWhen_userIsAdmin() {
        User original = createUserNormal();
        User modified = createUserNormal();
        modified.setPhoneNumber(new PhoneNumber(null));
        Mockito.when(mockUserRepo.getByID(original.getId())).thenReturn(original);
        service.update(original, original.getId(), modified);
        Mockito.verify(mockPhoneService, Mockito.times(0)).update(modified, original);
        original.setAdmin(true);
        modified.setAdmin(true);
        modified.setPhoneNumber(new PhoneNumber("0888222222"));
        service.update(original, original.getId(), modified);
        Mockito.verify(mockPhoneService, Mockito.times(1)).update(modified, original);
    }

    @Test
    void update_should_throw_when_userProvidedPhoneButNotAdmin() {
        User original = createUserNormal();
        User modified = createUserNormal();
        modified.setPhoneNumber(new PhoneNumber("0888222222"));
        Mockito.when(mockUserRepo.getByID(original.getId())).thenReturn(original);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(original, original.getId(), modified));
    }


    @Test
    void update_should_returnUserFromRepo_when_validationsPassed() {
        User mockUser = createUserNormal();
        mockUser.setPhoneNumber(new PhoneNumber(null));
        Mockito.when(mockUserRepo.getByID(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(mockUserRepo.update(mockUser.getId(), mockUser)).thenReturn(mockUser);
        User result = service.update(mockUser, mockUser.getId(), mockUser);
        Mockito.verify(mockUserRepo, Mockito.times(1)).update(mockUser.getId(), mockUser);
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void delete_should_throw_with_invalidID() {
        Assertions.assertThrows(InvalidParameter.class, () -> service.delete(createUserNormal(), -1));
    }

    @Test
    void delete_should_throw_when_requesterNotOwnerNorAdmin() {
        User requester = createUserNormal();
        requester.setId(2);
        requester.setUsername("requester");
        User owner = createUserNormal();
        requester.setId(3);
        requester.setUsername("owner");
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> service.delete(requester, owner.getId()));
    }

    @Test
    void delete_should_throw_when_userAlreadyDeleted() {
        User requester = createDeletedUser();
        User deleted = createDeletedUser();
        Mockito.when(mockUserRepo.getByID(deleted.getId())).thenReturn(deleted);
        Assertions.assertThrows(DuplicateEntityException.class, () -> service.delete(requester, deleted.getId()));
    }

    @Test
    void delete_should_returnUserFromRepo_when_requesterIsOwner() {
        User requester = createUserNormal();
        User deleted = createUserNormal();
        Mockito.when(mockUserRepo.getByID(deleted.getId())).thenReturn(deleted);
        Mockito.when(mockUserRepo.delete(deleted)).thenReturn(deleted);
        User result = service.delete(requester, deleted.getId());
        Mockito.verify(mockUserRepo, Mockito.times(1)).delete(deleted);
        Assertions.assertEquals(deleted, result);
    }

    @Test
    void delete_should_returnUserFromRepo_when_requesterIsAdmin() {
        User requester = createUserAdmin();
        requester.setUsername("requester");
        requester.setId(2);
        User deleted = createUserNormal();
        deleted.setId(3);
        requester.setUsername("owner");
        Mockito.when(mockUserRepo.getByID(4)).thenReturn(deleted);
        Mockito.when(mockUserRepo.delete(deleted)).thenReturn(deleted);
        User result = service.delete(requester, 4);
        Mockito.verify(mockUserRepo, Mockito.times(1)).delete(deleted);
        Assertions.assertEquals(deleted, result);
    }

    @Test
    void getNumberOfUsers_should_callRepository() {
        Mockito.when(mockUserRepo.getNumberOfUsers()).thenReturn(111L);
        long result = service.getNumberOfUsers();
        Mockito.verify(mockUserRepo, Mockito.times(1)).getNumberOfUsers();
        Assertions.assertEquals(111L, result);
    }

    @Test
    void getNumberOfInactiveUsers_should_callRepository() {
        Mockito.when(mockUserRepo.getNumberOfInactiveUsers()).thenReturn(222L);
        long result = service.getNumberOfInactiveUsers();
        Mockito.verify(mockUserRepo, Mockito.times(1)).getNumberOfInactiveUsers();
        Assertions.assertEquals(222L, result);
    }

    @Test
    void getNumberOfAdmins_should_callRepository() {
        Mockito.when(mockUserRepo.getNumberOfAdmins()).thenReturn(333L);
        long result = service.getNumberOfAdmins();
        Mockito.verify(mockUserRepo, Mockito.times(1)).getNumberOfAdmins();
        Assertions.assertEquals(333L, result);
    }

}
