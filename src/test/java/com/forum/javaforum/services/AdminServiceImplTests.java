package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.AdminRepository;
import com.forum.javaforum.repositories.contracts.PhoneRepository;
import com.forum.javaforum.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTests {

    @Mock
    AdminRepository mockAdminRepo;
    @Mock
    UserService mockUserService;
    @Mock
    PhoneRepository mockPhoneRepo;
    @InjectMocks
    AdminServiceImpl service;

    @Test
    void getAll_should_callRepository() {

        List<User> mockList = List.of(createUserNormal(), createUserAdmin(), createBlockedUser(), createDeletedUser());

        Mockito.when(mockAdminRepo.getAll(
                Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"),
                Optional.of("registration_date"), Optional.of("desc"))).thenReturn(mockList);

        List<User> result = service.getAll(
                Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"),
                Optional.of("registration_date"), Optional.of("desc"));

        Mockito.verify(mockAdminRepo,
                Mockito.times(1)).getAll(
                Optional.of("username"), Optional.of("firstName"), Optional.of("test@mail.com"),
                Optional.of("registration_date"), Optional.of("desc"));

        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getAllNonAdmins_should_callRepository() {
        List<User> mockList = List.of(createUserNormal(), createUserNormal());
        Mockito.when(mockAdminRepo.getAllNonAdmins(Mockito.anyString())).
                thenReturn(mockList);
        List<User> result = service.getAllNonAdmins("testUser");
        Mockito.verify(mockAdminRepo, Mockito.times(1)).
                getAllNonAdmins("testUser");
        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getAllBlockedUsers_should_callRepository() {
        List<User> mockList = List.of(createBlockedUser(), createBlockedUser());
        Mockito.when(mockAdminRepo.getAllBlockedUsers(Mockito.anyString())).
                thenReturn(mockList);
        List<User> result = service.getAllBlockedUsers("testUser");
        Mockito.verify(mockAdminRepo, Mockito.times(1)).
                getAllBlockedUsers("testUser");
        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getAllUnblockedUsers_should_callRepository() {
        List<User> mockList = List.of(createBlockedUser(), createBlockedUser());
        Mockito.when(mockAdminRepo.getAllUnblockedUsers(Mockito.anyString())).
                thenReturn(mockList);
        List<User> result = service.getAllUnblockedUsers("testUser");
        Mockito.verify(mockAdminRepo, Mockito.times(1)).
                getAllUnblockedUsers("testUser");
        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getAllNonDeletedUsers_should_callRepository() {
        List<User> mockList = List.of(createBlockedUser(), createBlockedUser());
        Mockito.when(mockAdminRepo.getAllNonDeletedUsers(Mockito.anyString())).
                thenReturn(mockList);
        List<User> result = service.getAllNonDeletedUsers("testUser");
        Mockito.verify(mockAdminRepo, Mockito.times(1)).
                getAllNonDeletedUsers("testUser");
        Assertions.assertEquals(mockList, result);
    }

    @Test
    void getById_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.getById(-1));
    }

    @Test
    void getById_should_returnUserFromRepo_when_matchExists() {
        User mockUser = createUserAdmin();
        Mockito.when(mockAdminRepo.getById(mockUser.getId())).thenReturn(mockUser);
        User result = service.getById(mockUser.getId());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).getById(mockUser.getId());
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void getByUsername_should_returnUserFromRepo_when_matchExists() {
        User mockUser = createUserAdmin();
        Mockito.when(mockAdminRepo.getByUsername(mockUser.getUsername())).thenReturn(mockUser);
        User result = service.getByUsername(mockUser.getUsername());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).getByUsername(mockUser.getUsername());
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void makeAdmin_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.makeAdmin(createUserAdmin(), -1));
    }

    @Test
    void makeAdmin_should_throw_when_requesterNotAdmin() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.makeAdmin(createUserNormal(), 1));
    }

    @Test
    void makeAdmin_should_retrieveTargetUserFromRepo_when_requesterIsAdmin() {
        User targetUser = createUserNormal();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        service.makeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockUserService, Mockito.times(1)).getById(targetUser.getId());
    }

    @Test
    void makeAdmin_should_throw_when_targetUserDeleted() {
        User targetUser = createDeletedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.makeAdmin(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void makeAdmin_should_throw_when_targetUserBlocked() {
        User targetUser = createBlockedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.makeAdmin(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void makeAdmin_should_throw_when_targetUserAlreadyAdmin() {
        User targetUser = createUserAdmin();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.makeAdmin(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void makeAdmin_should_returnUserFromRepo_when_validationsPassed() {
        User targetUser = createUserNormal();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Mockito.when(mockAdminRepo.makeAdmin(targetUser)).thenReturn(targetUser);
        User result = service.makeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).makeAdmin(targetUser);
        Assertions.assertEquals(targetUser, result);
    }

    @Test
    void removeAdmin_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.removeAdmin(createUserAdmin(), -1));
    }

    @Test
    void removeAdmin_should_throw_when_requesterNotAdmin() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeAdmin(createUserNormal(), 1));
    }

    @Test
    void removeAdmin_should_retrieveTargetUserFromRepo_when_requesterAndTargetValid() {
        User targetUser = createUserAdmin();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        service.removeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockUserService, Mockito.times(1)).getById(targetUser.getId());
    }

    @Test
    void removeAdmin_should_throw_when_targetUserDeleted() {
        User targetUser = createDeletedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeAdmin(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void removeAdmin_should_throw_when_targetUserNotAdmin() {
        User targetUser = createUserNormal();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.removeAdmin(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void removeAdmin_should_callPhoneRepo_when_targetHasPhoneNumber() {
        User targetUser = createUserAdmin();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        service.removeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockPhoneRepo, Mockito.times(0)).delete(targetUser.getPhoneNumber());
        targetUser.setPhoneNumber(new PhoneNumber("0888123456"));
        service.removeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockPhoneRepo, Mockito.times(1)).delete(targetUser.getPhoneNumber());
    }

    @Test
    void removeAdmin_should_returnUserFromRepo_when_validationsPassed() {
        User targetUser = createUserAdmin();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Mockito.when(mockAdminRepo.removeAdmin(targetUser)).thenReturn(targetUser);
        User result = service.removeAdmin(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).removeAdmin(targetUser);
        Assertions.assertEquals(targetUser, result);
    }

    @Test
    void blockUser_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.blockUser(createUserAdmin(), -1));
    }

    @Test
    void blockUser_should_throw_when_requesterNotAdmin() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.blockUser(createUserNormal(), 1));
    }

    @Test
    void blockUser_should_retrieveTargetUserFromRepo_when_requesterAndTargetValid() {
        User targetUser = createUserAdmin();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        service.blockUser(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockUserService, Mockito.times(1)).getById(targetUser.getId());
    }

    @Test
    void blockUser_should_throw_when_targetUserDeleted() {
        User targetUser = createDeletedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.blockUser(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void blockUser_should_throw_when_targetUserBlocked() {
        User targetUser = createBlockedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.blockUser(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void blockUser_should_returnUserFromRepo_when_validationsPassed() {
        User targetUser = createUserNormal();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Mockito.when(mockAdminRepo.blockUser(targetUser)).thenReturn(targetUser);
        User result = service.blockUser(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).blockUser(targetUser);
        Assertions.assertEquals(targetUser, result);
    }

    @Test
    void unblockUser_should_throw_when_invalidID() {
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.unblockUser(createUserAdmin(), -1));
    }

    @Test
    void unblockUser_should_throw_when_requesterNotAdmin() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.unblockUser(createUserNormal(), 1));
    }

    @Test
    void unblockUser_should_retrieveTargetUserFromRepo_when_requesterAndTargetValid() {
        User targetUser = createBlockedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        service.unblockUser(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockUserService, Mockito.times(1)).getById(targetUser.getId());
    }

    @Test
    void unblockUser_should_throw_when_targetUserDeleted() {
        User targetUser = createDeletedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.unblockUser(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void unblockUser_should_throw_when_targetUserNotBlocked() {
        User targetUser = createUserNormal();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.unblockUser(createUserAdmin(), targetUser.getId()));
    }

    @Test
    void unblockUser_should_returnUserFromRepo_when_validationsPassed() {
        User targetUser = createBlockedUser();
        Mockito.when(mockUserService.getById(targetUser.getId())).thenReturn(targetUser);
        Mockito.when(mockAdminRepo.unblockUser(targetUser)).thenReturn(targetUser);
        User result = service.unblockUser(createUserAdmin(), targetUser.getId());
        Mockito.verify(mockAdminRepo, Mockito.times(1)).unblockUser(targetUser);
        Assertions.assertEquals(targetUser, result);
    }
}
