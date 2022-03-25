package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.LikeRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.forum.javaforum.HelperForTisho.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTests {

    @Mock
    LikeRepository likeRepo;

    @Mock
    PostRepository postRepo;

    @InjectMocks
    LikeServiceImpl service;

    @Test
    public void getLikes_Should_ThrowException_When_PostDoesntExist() {
        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);


        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getLikes(Mockito.anyInt(), createUserNormal()));
    }

    @Test
    public void getLikes_Should_CallPostRepoGetById_When_PostDoesntExist() {
        Post post = createPostWithComments();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.getLikes(Mockito.anyInt(), createUserNormal());

        Mockito.verify(postRepo, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void getLikes_Should_ThrowException_WhenUserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getLikes(Mockito.anyInt(), user));
    }

    @Test
    public void getLikes_Should_Execute_WhenUserIsNotDeleted() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertDoesNotThrow(() -> service.getLikes(Mockito.anyInt(), user));
    }

    @Test
    public void getLikes_Should_CallLikeRepoGetLikes_When_ValidInput() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(post.getId()))
                .thenReturn(post);

        service.getLikes(post.getId(), user);

        Mockito.verify(likeRepo, Mockito.times(1))
                .getLikes(post.getId());
    }

    @Test
    public void addLikes_Should_ThrowException_When_PostDoesntExist() {
        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.addLike(Mockito.anyInt(), createUserNormal()));
    }

    @Test
    public void addLikes_Should_CallPostRepoGetById_When_PostExists() {
        Post post = createPostWithComments();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                        .thenReturn(Optional.empty());

        service.addLike(Mockito.anyInt(), createUserAdmin());

        Mockito.verify(postRepo, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void addLikes_Should_CallLikeRepoFindLike_When_PostExists() {
        Post post = createPostWithComments();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.empty());

        service.addLike(Mockito.anyInt(), createUserAdmin());

        Mockito.verify(likeRepo, Mockito.times(1))
                .findLike(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void addLikes_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addLike(Mockito.anyInt(), user));
    }

    @Test
    public void addLikes_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User user = createBlockedUser();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addLike(Mockito.anyInt(), user));
    }

    @Test
    public void addLikes_Should_Execute_When_UserIsNotDeletedOrBlocked() {
        Post post = createPostWithComments();
        User user = createUserNormal();
        user.setId(55);

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                        .thenReturn(Optional.empty());

        service.addLike(Mockito.anyInt(), user);

        Assertions.assertDoesNotThrow(() -> service.getLikes(Mockito.anyInt(), user));
    }

    @Test
    public void addLike_Should_ThrowException_When_LikeIsDuplicate() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(post.getId(), user.getId()))
                        .thenReturn(Optional.of(user));

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.addLike(post.getId(), user));

    }

    @Test
    public void addLikes_Should_CallLikeRepoAddLikes_When_ValidInput() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                        .thenReturn(Optional.empty());

        service.addLike(post.getId(), user);

        Mockito.verify(likeRepo, Mockito.times(1))
                .addLike(post.getId(), user.getId());
    }

    @Test
    public void removeLike_Should_ThrowException_When_PostDoesntExist() {
        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeLike(Mockito.anyInt(), createUserNormal()));
    }

    @Test
    public void removeLike_Should_CallPostRepoGetById_When_PostExists() {
        Post post = createPostWithComments();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(createUserNormal()));

        service.removeLike(Mockito.anyInt(), createUserAdmin());

        Mockito.verify(postRepo, Mockito.times(1))
                .getById(Mockito.anyInt());
    }

    @Test
    public void removeLike_Should_CallLikeRepoFindLike_When_PostExists() {
        Post post = createPostWithComments();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(createUserNormal()));

        service.removeLike(Mockito.anyInt(), createUserAdmin());

        Mockito.verify(likeRepo, Mockito.times(1))
                .removeLike(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void removeLike_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeLike(Mockito.anyInt(), user));
    }

    @Test
    public void removeLike_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User user = createBlockedUser();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeLike(Mockito.anyInt(), user));
    }

    @Test
    public void removeLike_Should_Execute_When_UserIsNotDeletedOrBlocked() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        service.removeLike(Mockito.anyInt(), user);

        Assertions.assertDoesNotThrow(() -> service.removeLike(Mockito.anyInt(), user));
    }

    @Test
    public void removeLike_Should_ThrowException_When_LikeDoesntExist() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(post.getId(), user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeLike(post.getId(), user));

    }

    @Test
    public void removeLike_Should_CallLikeRepoRemoveLike_When_ValidInput() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        Mockito.when(postRepo.getById(post.getId()))
                .thenReturn(post);

        Mockito.when(likeRepo.findLike(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        service.removeLike(post.getId(), user);

        Mockito.verify(likeRepo, Mockito.times(1))
                .removeLike(post.getId(), user.getId());
    }

}
