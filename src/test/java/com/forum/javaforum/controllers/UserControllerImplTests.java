package com.forum.javaforum.controllers;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.*;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerImplTests {

    @MockBean
    UserService mockService;
    @MockBean
    UserMapper mockMapper;
    @MockBean
    AuthenticationHelper mockAuthenticationHelper;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getAllUsers_should_throw_whenParamsInvalid() throws Exception {

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(createUserNormal());

        Mockito.when(mockService.getAll(
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty())).
                thenThrow(InvalidParameter.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")).
                andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void getAllUsers_should_returnOK_whenParamsValid() throws Exception {

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(createUserNormal());

        List<User> users = List.of(
                createUserNormal(), createUserAdmin(),
                createBlockedUser(), createDeletedUser());

        Mockito.when(mockService.getAll(
                        Optional.of("username"),
                        Optional.of("firstName"),
                        Optional.of("test@mail.com"),
                        Optional.of("registration_date"),
                        Optional.of("desc"))).
                thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users").
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
    public void getById_should_throwUnauthorized_when_requesterNotRegistered() throws Exception {
        HttpHeaders tesHeaders = new HttpHeaders();
        User user = createUserNormal();
        Mockito.when(mockAuthenticationHelper.tryGetUser(tesHeaders)).thenCallRealMethod();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()).headers(tesHeaders))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getById_should_throwBadRequest_when_invalidParameterErrorArises() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.getById(user.getId())).
                thenThrow(InvalidParameter.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getById_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.getById(user.getId())).
                thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId())).
                andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getById_should_returnUserToDTOOut_when_requesterNotOwner() throws Exception {
        User requester = createUserNormal();
        requester.setId(10);
        User user = createUserNormal();
        user.setId(20);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(requester);
        Mockito.when(mockService.getById(user.getId())).
                thenReturn(user);
        Mockito.when(mockMapper.UserToDTOOut(user)).
                thenReturn(getUserToDTOOut(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                getById(user.getId());
    }

    @Test
    public void getById_should_returnUserToDTOOutOwner_when_requesterIsOwner() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);
        Mockito.when(mockService.getById(user.getId())).
                thenReturn(user);
        Mockito.when(mockMapper.UserToDTOOutOwner(user)).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                getById(user.getId());
    }

    @Test
    public void createUser_should_throwForbidden_when_userAlreadyRegistered() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class))).
                thenReturn(user);
        Mockito.when(mockService.create(Mockito.any(HttpHeaders.class), Mockito.any(User.class))).
                thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createUser_should_throwConflict_when_usernameOrEmailAlreadyTaken() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class))).
                thenReturn(user);
        Mockito.when(mockService.create(Mockito.any(HttpHeaders.class), Mockito.any(User.class))).
                thenThrow(DuplicateEntityException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void createUser_should_returnFromService_when_NoErrors() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class))).
                thenReturn(user);
        Mockito.when(mockService.create(Mockito.any(HttpHeaders.class), Mockito.any(User.class))).
                thenReturn(user);
        Mockito.when(mockService.create(Mockito.any(HttpHeaders.class), Mockito.any(User.class))).
                thenReturn(user);
        Mockito.when(mockMapper.UserToDTOOut(user)).thenReturn(getUserToDTOOut(user));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                create(Mockito.any(HttpHeaders.class), Mockito.any(User.class));
    }


    @Test
    public void updateUser_should_throwForbidden_when_userNotOwner() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockService.update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class))).
                thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void updateUser_should_throwBadRequest_when_userAttributeInvalid() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockService.update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class))).
                thenThrow(InvalidParameter.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateUser_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockService.update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class))).
                thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateUser_should_throwConflict_when_usernameOrEmailAlreadyTaken() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockService.update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class))).
                thenThrow(DuplicateEntityException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void updateUser_should_returnFromService_when_NoErrors() throws Exception {
        User user = createUserNormal();
        UserDTOInImpl dto = getUserDtoIn(user);

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockMapper.toObject(Mockito.any(UserDTOInImpl.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockService.update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class))).
                thenReturn(user);
        Mockito.when(mockMapper.UserToDTOOut(user)).thenReturn(getUserToDTOOut(user));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).
                update(Mockito.any(User.class), Mockito.anyInt(), Mockito.any(User.class));
    }

    @Test
    public void deleteUser_should_throwForbidden_when_userNotOwnerOrAdmin() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.delete(Mockito.any(User.class), Mockito.anyInt())).
                thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteUser_should_throwBadRequest_when_userAlreadyDeleted() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.delete(Mockito.any(User.class), Mockito.anyInt())).
                thenThrow(InvalidParameter.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteUser_should_throwNotFound_when_userNonExisting() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.delete(Mockito.any(User.class), Mockito.anyInt())).
                thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteUser_should_throwConflict_when_duplicateEntityErrorArises() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.delete(Mockito.any(User.class), Mockito.anyInt())).
                thenThrow(DuplicateEntityException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void deleteUser_should_returnFromService_when_NoErrors() throws Exception {
        User user = createUserNormal();

        Mockito.when(mockAuthenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class))).
                thenReturn(user);
        Mockito.when(mockService.delete(Mockito.any(User.class), Mockito.anyInt())).
                thenReturn(user);
        Mockito.when(mockMapper.UserToDTOOutOwner(user)).
                thenReturn(getUserToDTOOutOwner(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

        Mockito.verify(mockService, Mockito.times(1)).delete(user, user.getId());
    }

    @Test
    void getNumberOfUsers_should_returnFromService() throws Exception {
        Mockito.when(mockService.getNumberOfUsers()).thenReturn(111L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/count"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("111"));

        Mockito.verify(mockService, Mockito.times(1)).getNumberOfUsers();
    }

    @Test
    void getNumberOfInactiveUsers_should_returnFromService() throws Exception {
        Mockito.when(mockService.getNumberOfInactiveUsers()).thenReturn(222L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/countinactive"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("222"));

        Mockito.verify(mockService, Mockito.times(1)).getNumberOfInactiveUsers();
    }

    @Test
    void getNumberOfAdmins_should_returnFromService() throws Exception {
        Mockito.when(mockService.getNumberOfAdmins()).thenReturn(333L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/countadmins"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("333"));

        Mockito.verify(mockService, Mockito.times(1)).getNumberOfAdmins();
    }


}
