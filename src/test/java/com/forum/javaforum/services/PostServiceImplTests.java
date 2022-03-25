package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.CommentRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.services.contracts.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static com.forum.javaforum.HelperForTisho.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    PostServiceImpl service;

    @Test
    void getAllWithoutUser_Should_CallRepository() {
        Post post = createPostWithComments();
        post.setTags(Set.of(new Tag(1, "random")));
        List<Post> mockResult = List.of(post);

        Mockito.when(postRepository.getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.empty()))
                .thenReturn(mockResult);

        List<Post> actualResult = service.getAll(Optional.of("Tihomir"), Optional.of("element"),
                Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                Optional.empty());

        Mockito.verify(postRepository, Mockito.times(1))
                .getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.empty());

        Assertions.assertEquals(mockResult, actualResult);
    }

    @Test
    void getAllWithoutUser_Should_ThrowException_When_LimitIsInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.of("-123")));
    }

    @Test
    void getAllWitUser_Should_CallRepository() {
        Post post = createPostWithComments();
        post.setTags(Set.of(new Tag(1, "random")));
        List<Post> mockResult = List.of(post);
        User user = createUserNormal();

        Mockito.when(postRepository.getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.of(1l)))
                .thenReturn(mockResult);

        List<Post> actualResult = service.getAll(Optional.of("Tihomir"), Optional.of("element"),
                Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                Optional.of("1"), user);

        Mockito.verify(postRepository, Mockito.times(1))
                .getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.of(1l));

        Assertions.assertEquals(mockResult, actualResult);
    }

    @Test
    void getAllWitUser_Should_ThrowException_When_UserIsDeleted() {
        User user = createDeletedUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getAll(Optional.of("Tihomir"), Optional.of("element"),
                        Optional.of("random"), Optional.of("date"), Optional.of("desc"),
                        Optional.of("1"), user));
    }

    @Test
    void getMostCommented_Should_CallGetAll() {
        PostServiceImpl test = Mockito.spy(service);
        test.getMostCommented();
        Mockito.verify(test, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("comments"), Optional.of("desc"), Optional.of("10"));
    }

    @Test
    void getMostLiked_Should_CallGetAll() {
        PostServiceImpl test = Mockito.spy(service);
        test.getMostLiked();
        Mockito.verify(test, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("likes"), Optional.of("desc"), Optional.of("10"));

    }

    @Test
    void getMostRecent_Should_CallGetAll() {
        PostServiceImpl test = Mockito.spy(service);
        test.getMostRecent();
        Mockito.verify(test, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("date"), Optional.of("desc"), Optional.of("10"));
    }

    @Test
    public void getById_Should_ThrowException_When_UserIsDeleted() {
        User user = createDeletedUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(1, user));
    }

    @Test
    public void getById_Should_CallRepo_When_UserNotDeleted() {
        User user = createUserNormal();

        service.getById(1, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .getById(1);
    }


    @Test
    public void create_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.create(post, user));
    }

    @Test
    public void create_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User user = createBlockedUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.create(post, user));
    }

    @Test
    public void create_Should_CallRepo_When_UserNotDeleted() {
        Post post = createPostWithComments();
        User user = createUserNormal();

        service.create(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .create(post);
    }

    @Test
    public void update_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(post, user));
    }

    @Test
    public void update_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User user = createBlockedUser();
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(post, user));
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Post post = createPostWithComments();

        User author = createUserNormal();
        author.setId(4);
        author.setUsername("authorOfPost");
        User other = createUserNormal();
        other.setId(5);
        other.setUsername("someoneElse");
        post.setAuthor(author);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);


        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(post, other));
    }

    @Test
    public void update_Should_Execute_When_UserIsAuthor() {
        Post post = createPostWithComments();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.update(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);

        Assertions.assertDoesNotThrow(() -> service.update(post, user));
    }

    @Test
    public void update_Should_Execute_When_UserIsAdmin() {
        Post post = createPostWithComments();
        User user = createUserAdmin();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.update(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);

        Assertions.assertDoesNotThrow(() -> service.update(post, user));
    }

    @Test
    public void update_Should_CallRepo_When_InputIsValid() {
        Post post = createPostWithComments();
        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.update(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsDeleted() {
        Post post = createPostWithComments();
        User user = createDeletedUser();
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsBlocked() {
        Post post = createPostWithComments();
        User user = createBlockedUser();
        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Post post = createPostWithComments();

        User author = createUserNormal();
        author.setId(4);
        author.setUsername("authorOfPost");
        User other = createUserNormal();
        other.setId(5);
        other.setUsername("someoneElse");
        post.setAuthor(author);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(post.getId(), other));
    }

    @Test
    public void delete_Should_Execute_When_UserIsAuthor() {
        Post post = createPostWithComments();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(new Tag(1, "random"));
        post.setTags(tags);

        User user = createUserNormal();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.delete(post.getId(), user);

        Assertions.assertDoesNotThrow(() -> service.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_Execute_When_UserIsAdmin() {
        Post post = createPostWithComments();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(new Tag(1, "random"));
        post.setTags(tags);

        User user = createUserAdmin();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.delete(post.getId(), user);

        Assertions.assertDoesNotThrow(() -> service.delete(post.getId(), user));
    }

    @Test
    public void delete_Should_CallRepo_When_InputIsValid() {
        Post post = createPostWithComments();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(new Tag(1, "random"));
        post.setTags(tags);

        User user = createUserAdmin();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        service.delete(post.getId(), user);

        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);

        Mockito.verify(postRepository, Mockito.times(1))
                .delete(post);

    }

    @Test
    public void delete_Should_DeleteAllData_When_InputIsValid() {
        Post post = createPostWithComments();
        HashSet<Tag> tags = new HashSet<>();
        tags.add(new Tag(1, "random"));
        post.setTags(tags);

        User user = createUserAdmin();
        post.setAuthor(user);

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Post result = service.delete(post.getId(), user);

        HashSet<User> emptyLikes = new HashSet<>();
        HashSet<Tag> emptyTags = new HashSet<>();
        HashSet<Comment> emptyComments = new HashSet<>();

        Assertions.assertEquals(emptyLikes, post.getLikes());
        Assertions.assertEquals(emptyTags, post.getTags());
        Assertions.assertEquals(emptyComments, post.getComments());
        Assertions.assertEquals(result, post);
    }

    @Test
    public void getNumberOfPosts_Should_CallRepoMethod() {
        service.getNumberOfPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getNumberOfPosts();
    }


}
