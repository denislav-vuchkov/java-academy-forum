package com.forum.javaforum.controllers;


import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.PostDto;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.PostMapper;
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
public class PostControllerTests {

    @MockBean
    PostService service;

    @MockBean
    PostMapper mapper;

    @MockBean
    AuthenticationHelper authenticationHelper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAll_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getAll_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), user))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAll_Should_Throw_BadRequest_When_InvalidSearchAndSort() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), user))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getMostCommented_should_return_statusOK() throws Exception {
        List<Post> posts = List.of(createPostWithComments());

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.of("comments"), Optional.of("desc"), Optional.of("10")))
                .thenReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/mostCommented"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getMostLiked_should_return_statusOK() throws Exception {
        List<Post> posts = List.of(createPostWithComments());

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.of("likes"), Optional.of("desc"), Optional.of("10")))
                .thenReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/mostLiked"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getMostRecent_should_return_statusOK() throws Exception {
        List<Post> posts = List.of(createPostWithComments());

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.of("date"), Optional.of("desc"), Optional.of("10")))
                .thenReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/mostRecent"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getById_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getById_Should_Throw_Forbidden_When_UserIsDeleted() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getById(1, user))
                        .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getById_Should_Throw_NotFound_When_PostNotFound() throws Exception {
        User user = createUserNormal();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getById(1, user))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void create_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createPostDTOIn())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void create_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(PostDto.class), Mockito.any(User.class)))
                        .thenReturn(post);

        Mockito.doThrow(UnauthorizedOperationException.class)
                        .when(service).create(Mockito.any(Post.class), Mockito.any(User.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void create_Should_Execute_When_ValidInput() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(PostDto.class), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.doNothing().when(service).create(Mockito.any(Post.class), Mockito.any(User.class));
        Mockito.when(service.getById(Mockito.anyInt(),Mockito.any(User.class))).thenReturn(post);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(post.getContent()));
    }

    @Test
    public void update_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createPostDTOIn())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void update_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(service.getById(Mockito.anyInt(), Mockito.any(User.class)))
                        .thenReturn(post);

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(Post.class), Mockito.any(PostDto.class), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.when(service.update(Mockito.any(Post.class), Mockito.any(User.class)))
                        .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void update_Should_Throw_Forbidden_When_PostDoesNotExist() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(service.getById(Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(Post.class), Mockito.any(PostDto.class), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.when(service.update(Mockito.any(Post.class), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void delete_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createPostDTOIn())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void delete_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(service.getById(Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.delete(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void delete_Should_Throw_Forbidden_When_PostDoesNotExist() throws Exception {
        User user = createDeletedUser();
        PostDto postDto = createPostDTOIn();
        Post post = createPostWithComments();

        Mockito.when(service.getById(Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(post);

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.delete(Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getNumberOfPosts_should_return_statusOK() throws Exception {
        Mockito.when(service.getNumberOfPosts())
                .thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/count"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
