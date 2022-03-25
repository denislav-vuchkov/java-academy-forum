package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.repositories.contracts.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.forum.javaforum.HelperForTisho.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTests {

    @Mock
    TagRepository tagRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    TagServiceImpl service;


    @Test
    public void getTags_Should_Throw_When_PostDoesntExist() {
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getTags(1, createUserNormal()));
    }

    @Test
    public void getTags_Should_CallGetById() {
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.getTags(1, createUserNormal());

        Mockito.verify(postRepository, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getTags_Should_CallRepository() {
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.getTags(1, createUserNormal());

        Mockito.verify(tagRepository, Mockito.times(1))
                .getTags(Mockito.anyInt());
    }

    @Test
    public void getTags_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User deletedUser = createDeletedUser();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getTags(1, deletedUser));
    }


    @Test
    public void addTag_Should_Throw_When_PostDoesntExist() {
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.addTag(1, "random", createUserNormal()));
    }

    @Test
    public void addTag_Should_CallGetById() {
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.addTag(1, "random", createUserAdmin());

        Mockito.verify(postRepository, Mockito.times(2))
                .getById(Mockito.anyInt());
    }

    @Test
    public void addTag_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User deletedUser = createDeletedUser();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addTag(1, "random", deletedUser));
    }

    @Test
    public void addTag_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User blockedUser = createBlockedUser();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addTag(1, "random", blockedUser));
    }

    @Test
    public void addTag_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addTag(1, "random", user));
    }

    @Test
    public void addTag_Should_Execute_When_UserIsAuthor() {
        Post post = createPostWithComments();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertDoesNotThrow(() -> service.addTag(1, "random", user));
    }

    @Test
    public void addTag_Should_Execute_When_UserIsAdmin() {
        Post post = createPostWithComments();
        User admin = createUserAdmin();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertDoesNotThrow(() -> service.addTag(1, "random", admin));
    }

    @Test
    public void addTag_Should_ThrowException_When_TagIsDuplicate() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                        .thenReturn(Optional.of(new Tag(1, "random")));

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.addTag(1, "random", user));
    }

    @Test
    public void addTag_Should_CallTagRepoFindTag_When_TagIsNotDuplicate() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        service.addTag(1, "something", user);

        Mockito.verify(tagRepository, Mockito.times(1))
                        .findTag(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void addTag_Should_CallTagRepoCreateTag_When_TagIsNotDuplicate() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        service.addTag(1, "something", user);

        Mockito.verify(tagRepository, Mockito.times(1))
                .createTag("something");
    }

    @Test
    public void addTag_Should_CallTagRepoAddTag_When_TagIsNotDuplicate() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        service.addTag(1, "something", user);

        Mockito.verify(tagRepository, Mockito.times(1))
                .addTag(1, "something");
    }

    @Test
    public void removeTag_Should_Throw_When_PostDoesntExist() {
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeTag(1, "random", createUserNormal()));
    }

    @Test
    public void removeTag_Should_CallGetById() {
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(new Tag(1, "random")));

        service.removeTag(1, "random", createUserAdmin());

        Mockito.verify(postRepository, Mockito.times(2))
                .getById(Mockito.anyInt());
    }

    @Test
    public void removeTag_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User deletedUser = createDeletedUser();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeTag(1, "random", deletedUser));
    }

    @Test
    public void removeTag_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User blockedUser = createBlockedUser();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeTag(1, "random", blockedUser));
    }

    @Test
    public void removeTag_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeTag(1, "random", user));
    }

    @Test
    public void removeTag_Should_Execute_When_UserIsAuthor() {
        Post post = createPostWithComments();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(new Tag(1, "random")));

        Assertions.assertDoesNotThrow(() -> service.removeTag(1, "random", user));
    }

    @Test
    public void removeTag_Should_Execute_When_UserIsAdmin() {
        Post post = createPostWithComments();
        User admin = createUserAdmin();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(new Tag(1, "random")));

        Assertions.assertDoesNotThrow(() -> service.removeTag(1, "random", admin));
    }

    @Test
    public void removeTag_Should_ThrowException_When_TagDoesNotExist() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeTag(1, "random", user));
    }

    @Test
    public void removeTag_Should_CallTagRepoRemoveTag_When_TagExists() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setTags(Set.of(new Tag(1, "random")));

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(tagRepository.findTag(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(new Tag(1, "random")));

        service.removeTag(1, "random", user);

        Mockito.verify(tagRepository, Mockito.times(1))
                .removeTag(1, "random");
    }

}
