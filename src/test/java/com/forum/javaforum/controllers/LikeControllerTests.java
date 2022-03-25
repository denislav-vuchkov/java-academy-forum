package com.forum.javaforum.controllers;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.LikeService;
import com.forum.javaforum.utilities.AuthenticationHelper;
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


import static com.forum.javaforum.HelperForTisho.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LikeControllerTests {

    @MockBean
    LikeService service;

    @MockBean
    AuthenticationHelper authenticationHelper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getLikes_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getAll_Should_Throw_NotFound_When_PostDoesntExist() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getLikes(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getLikes_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getLikes(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addLike_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void addLike_Should_Throw_Forbidden_When_PostDoesntExist() throws Exception {
        User user = createUserNormal();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.addLike(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addLike_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.addLike(Mockito.anyInt(), Mockito.any(User.class)))
                        .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addLike_Should_Throw_Conflict_When_UserHasAlreadyLikedThePost() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.addLike(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(DuplicateEntityException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void removeLike_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void removeLike_Should_Throw_Forbidden_When_PostDoesntExist() throws Exception {
        User user = createUserNormal();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.removeLike(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void removeLike_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.removeLike(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/likes", 1))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


}
