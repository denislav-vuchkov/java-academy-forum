package com.forum.javaforum.controllers;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.AdminService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerImplTests {

    @MockBean
    AdminService mockService;
    @MockBean
    UserMapper mockMapper;
    @MockBean
    AuthenticationHelper mockAuthenticationHelper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getAll_should_throw_whenParamsInvalid() throws Exception {

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(createUserAdmin());

        Mockito.when(mockService.getAll(
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty())).
                thenThrow(InvalidParameter.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins")).
                andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAll_should_returnOK_whenParamsValid() throws Exception {

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(createUserAdmin());

        List<User> admins = List.of( createUserAdmin());

        Mockito.when(mockService.getAll(
                        Optional.of("username"),
                        Optional.of("firstName"),
                        Optional.of("test@mail.com"),
                        Optional.of("registration_date"),
                        Optional.of("desc"))).
                thenReturn(admins);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins").
                        param("username", "testUser").
                        param("firstName", "testName").
                        param("email", "test@mail.com").
                        param("sortBy", "registration_date").
                        param("sortOrder", "desc")).
                andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(mockService, Mockito.times(1)).getAll(
                Optional.of("testUser"),
                Optional.of("testName"),
                Optional.of("test@mail.com"),
                Optional.of("registration_date"),
                Optional.of("desc"));
    }

    @Test
    public void getById_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.getById(user.getId())).
                thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", user.getId())).
                andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getById_should_throwBadRequest_when_givenNonAdminId() throws Exception {
        User admin = createUserAdmin();
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);

        Mockito.when(mockService.getById(admin.getId())).
                thenThrow(IllegalArgumentException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getById_should_throwBadRequest_when_givenInvalidId() throws Exception {
        User admin = createUserAdmin();
        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);

        Mockito.when(mockService.getById(admin.getId())).
                thenThrow(InvalidParameter.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getById_should_returnUserToDTOOut_when_requesterNotAdmin() throws Exception {
        User user = createUserNormal();
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);
        Mockito.when(mockService.getById(admin.getId())).
                thenReturn(admin);
        Mockito.when(mockMapper.UserToDTOOut(admin)).
                thenReturn(getUserToDTOOut(admin));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(admin.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                getById(admin.getId());
    }

    @Test
    public void getById_should_returnUserToDTOOutOwner_when_requesterIsAdmin() throws Exception {
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(admin);
        Mockito.when(mockService.getById(admin.getId())).
                thenReturn(admin);
        Mockito.when(mockMapper.UserToDTOOutOwner(admin)).
                thenReturn(getUserToDTOOutOwner(admin));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(admin.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                getById(admin.getId());
    }

    @Test
    public void makeAdmin_should_throwForbidden_when_requesterNotAdmin() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.makeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(UnauthorizedOperationException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/make/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void makeAdmin_should_throwBadRequest_when_givenInvalidId() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.makeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(InvalidParameter.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/make/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void makeAdmin_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.makeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(EntityNotFoundException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/make/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void makeAdmin_should_throwConflict_when_userAlreadyAdmin() throws Exception {
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);
        Mockito.when(mockService.makeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(DuplicateEntityException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(admin));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/make/{id}",admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void removeAdmin_should_throwForbidden_when_requesterNotAdmin() throws Exception {
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);
        Mockito.when(mockService.removeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(UnauthorizedOperationException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(admin));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/remove/{id}",admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void removeAdmin_should_throwBadRequest_when_givenInvalidIdOrNonAdminId() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.removeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(InvalidParameter.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/remove/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void removeAdmin_should_throwNotFound_when_userNonExisting() throws Exception {
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);
        Mockito.when(mockService.removeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(EntityNotFoundException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(admin));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/remove/{id}",admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void removeAdmin_should_throwConflict_when_duplicateEntityErrorArises() throws Exception {
        User admin = createUserAdmin();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(admin);
        Mockito.when(mockService.removeAdmin(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(DuplicateEntityException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(admin));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/remove/{id}",admin.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void blockUser_should_throwForbidden_when_requesterNotAdmin() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.blockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(UnauthorizedOperationException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/block/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void blockUser_should_throwBadRequest_when_givenInvalidId() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.blockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(InvalidParameter.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/block/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void blockUser_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.blockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(EntityNotFoundException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/block/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void blockUser_should_throwConflict_when_userAlreadyAdmin() throws Exception {
        User blocked = createBlockedUser();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(blocked);
        Mockito.when(mockService.blockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(DuplicateEntityException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(blocked));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/block/{id}",blocked.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void unblockUser_should_throwForbidden_when_requesterNotAdmin() throws Exception {
        User blocked = createBlockedUser();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(blocked);
        Mockito.when(mockService.unblockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(UnauthorizedOperationException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(blocked));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/unblock/{id}",blocked.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void unblockUser_should_throwBadRequest_when_givenInvalidIdOrNonBlockedId() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.unblockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(InvalidParameter.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/unblock/{id}",user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void unblockUser_should_throwNotFound_when_userNonExisting() throws Exception {
        User blocked = createBlockedUser();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(blocked);
        Mockito.when(mockService.unblockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(EntityNotFoundException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(blocked));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/unblock/{id}",blocked.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void unblockUser_should_throwConflict_when_duplicateEntityErrorArises() throws Exception {
        User blocked = createBlockedUser();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(blocked);
        Mockito.when(mockService.unblockUser(Mockito.any(User.class),Mockito.anyInt())).
                thenThrow(DuplicateEntityException.class);
        Mockito.when(mockMapper.UserToDTOOutOwner(Mockito.any(User.class))).
                thenReturn(getUserToDTOOutOwner(blocked));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/unblock/{id}",blocked.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
