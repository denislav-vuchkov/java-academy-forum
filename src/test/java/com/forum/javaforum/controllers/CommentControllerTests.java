package com.forum.javaforum.controllers;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.CommentService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import com.forum.javaforum.utilities.mappers.CommentMapper;
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

import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;
import static com.forum.javaforum.HelperForTisho.asJsonString;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTests {

    @MockBean
    CommentService service;

    @MockBean
    CommentMapper mapper;

    @MockBean
    AuthenticationHelper authenticationHelper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getAll_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/comments"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getAll_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), user))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/comments"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void getAll_Should_Throw_BadRequest_When_InvalidSearchAndSort() throws Exception {
        User user = createDeletedUser();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), user))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/comments"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addComment_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{id}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createCommentDto())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void addComment_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(CommentDto.class), Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(comment);

        Mockito.doThrow(UnauthorizedOperationException.class)
                .when(service).addComment(Mockito.any(Comment.class), Mockito.any(User.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{id}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addComment_Should_Throw_Forbidden_When_PostDoesntExist() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(CommentDto.class), Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(comment);

        Mockito.doThrow(EntityNotFoundException.class)
                .when(service).addComment(Mockito.any(Comment.class), Mockito.any(User.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{id}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addComment_Should_Execute_When_ValidInput() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(mapper.fromDto(Mockito.any(CommentDto.class), Mockito.anyInt(), Mockito.any(User.class)))
                .thenReturn(comment);

        Mockito.doNothing().when(service).addComment(Mockito.any(Comment.class), Mockito.any(User.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/posts/{id}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(comment.getContent()));
    }


    @Test
    public void update_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{id}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createCommentDto())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void update_Should_Throw_Forbidden_When_PostDoesNotExist() throws Exception {
        User user = createUserNormal();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getCommentIfExists(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(comment);

        Mockito.when(mapper.fromDto(Mockito.any(Comment.class), Mockito.any(CommentDto.class)))
                .thenReturn(comment);

        Mockito.when(service.editComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Comment.class) ,Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void update_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getCommentIfExists(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(comment);

        Mockito.when(mapper.fromDto(Mockito.any(Comment.class), Mockito.any(CommentDto.class)))
                .thenReturn(comment);

        Mockito.when(service.editComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Comment.class) ,Mockito.any(User.class)))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void update_Should_Throw_BadRequest_When_InvalidArgumentProvided() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.getCommentIfExists(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(comment);

        Mockito.when(mapper.fromDto(Mockito.any(Comment.class), Mockito.any(CommentDto.class)))
                .thenReturn(comment);

        Mockito.when(service.editComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Comment.class) ,Mockito.any(User.class)))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void delete_Should_Throw_Unauthorised_WhenMissingHeader() throws Exception {
        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenCallRealMethod();

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(createCommentDto())))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void delete_Should_Throw_Forbidden_When_PostDoesNotExist() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.removeComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void delete_Should_Throw_Forbidden_When_UserIsDeletedBlockedOrNotAuthor() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.removeComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(UnauthorizedOperationException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void delete_Should_Throw_BadRequest_When_InvalidArugmentProvided() throws Exception {
        User user = createDeletedUser();
        CommentDto commentDto = createCommentDto();

        Mockito.when(authenticationHelper.tryGetUser(Mockito.any(HttpHeaders.class)))
                .thenReturn(user);

        Mockito.when(service.removeComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(User.class)))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/posts/{postID}/comments/{commentID}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getNumberOfPosts_should_return_statusOK() throws Exception {
        Mockito.when(service.getNumberOfComments())
                .thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/count"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
