package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.CommentRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
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
public class CommentServiceImplTests {

    @Mock(lenient = true)
    CommentRepository commentRepository;


    @Mock(lenient = true)
    PostRepository postRepository;

    @InjectMocks
    CommentServiceImpl service;


    @Test
    void getAll_should_callRepository() {
        List<Comment> mockResult = List.of(createComment());
        User user = createUserNormal();

        Mockito.when(commentRepository.getAll(
                Optional.of(user.getUsername()), Optional.of(1), Optional.of("Emili"),
                        Optional.of("date"), Optional.of("desc")))
                .thenReturn(mockResult);

        List<Comment> actualResult = service.getAll(
                Optional.of(user.getUsername()), Optional.of(1), Optional.of("Emili"),
                Optional.of("date"), Optional.of("desc"), user);

        Mockito.verify(commentRepository, Mockito.times(1)).getAll(
                        Optional.of(user.getUsername()), Optional.of(1), Optional.of("Emili"),
                        Optional.of("date"), Optional.of("desc"));

        Assertions.assertEquals(mockResult, actualResult);
    }


    @Test
    public void addComment_Should_ThrowException_When_PostDoesntExist() {
        Comment comment = createComment();
        User user = createUserAdmin();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.addComment(comment, user));
    }

    @Test
    public void addComment_Should_CallGetByID() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        service.addComment(comment, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .getById(comment.getPostID());
    }

    @Test
    public void addComment_Should_ThrowException_When_UserIsDeleted() {
        Comment comment = createComment();
        User user = createDeletedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addComment(comment, user));
    }

    @Test
    public void addComment_Should_ThrowException_When_UserIsBlocked() {
        Comment comment = createComment();
        User user = createBlockedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.addComment(comment, user));
    }

    @Test
    public void addComment_Should_CallRepo_When_ValidInput() {
        Comment comment = createComment();
        User user = createUserNormal();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        service.addComment(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .addComment(comment);
    }


    @Test
    public void editComment_Should_ThrowException_When_PostDoesntExist() {
        Comment comment = createComment();
        User user = createUserAdmin();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.editComment(1, 1, comment, user));
    }

    @Test
    public void editComment_Should_CallGetByID() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getPostID()))
                .thenReturn(comment);

        service.editComment(1, 1, comment, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .getById(comment.getPostID());
    }

    @Test
    public void editComment_Should_ThrowException_When_InvalidCommentId() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(2))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.editComment(1, 2, comment, user));
    }

    @Test
    public void editComment_Should_ThrowException_When_PostAndCommentIDDontMatch() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.editComment(2, 1, comment, user));
    }


    @Test
    public void editComment_Should_ThrowException_When_UserIsDeleted() {
        Comment comment = createComment();
        User user = createDeletedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.editComment(1, 1, comment, user));
    }

    @Test
    public void editComment_Should_ThrowException_When_UserIsBlocked() {
        Comment comment = createComment();
        User user = createBlockedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.editComment(1, 1, comment, user));
    }

    @Test
    public void editComment_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Comment comment = createComment();
        User user = createUserNormal();
        user.setId(99);
        user.setUsername("someoneElse");
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.editComment(1, 1, comment, user));
    }

    @Test
    public void editComment_Should_Execute_When_UserIsAuthor() {
        Comment comment = createComment();
        User user = createUserNormal();
        comment.setAuthor(user);
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertDoesNotThrow(() ->
                service.editComment(1, 1, comment, user));
    }

    @Test
    public void editComment_Should_Execute_When_UserIsAdmin() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertDoesNotThrow(() ->
                service.editComment(1, 1, comment, user));
    }


    @Test
    public void editComment_Should_EditCommentRepo() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getPostID()))
                .thenReturn(comment);

        service.editComment(1, 1, comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .editComment(comment);
    }


    @Test
    public void removeComment_Should_ThrowException_When_PostDoesntExist() {
        Comment comment = createComment();
        User user = createUserAdmin();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_CallGetByID() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getPostID()))
                .thenReturn(comment);

        service.removeComment(1, 1, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .getById(comment.getPostID());
    }

    @Test
    public void removeComment_Should_ThrowException_When_InvalidCommentId() {
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(Mockito.anyInt()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_ThrowException_When_PostAndCommentIDDontMatch() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.removeComment(2, 1, user));
    }


    @Test
    public void removeComment_Should_ThrowException_When_UserIsDeleted() {
        Comment comment = createComment();
        User user = createDeletedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_ThrowException_When_UserIsBlocked() {
        Comment comment = createComment();
        User user = createBlockedUser();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_ThrowException_When_UserIsNotAuthorOrAdmin() {
        Comment comment = createComment();
        User user = createUserNormal();
        user.setId(99);
        user.setUsername("someoneElse");
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_Execute_When_UserIsAuthor() {
        Comment comment = createComment();
        User user = createUserNormal();
        comment.setAuthor(user);
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertDoesNotThrow(() ->
                service.removeComment(1, 1, user));
    }

    @Test
    public void removeComment_Should_Execute_When_UserIsAdmin() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getId()))
                .thenReturn(comment);

        Assertions.assertDoesNotThrow(() ->
                service.removeComment(1, 1, user));
    }


    @Test
    public void removeComment_Should_EditCommentRepo() {
        Comment comment = createComment();
        User user = createUserAdmin();
        Post post = createPostWithComments();

        Mockito.when(postRepository.getById(comment.getPostID()))
                .thenReturn(post);

        Mockito.when(commentRepository.getById(comment.getPostID()))
                .thenReturn(comment);

        service.removeComment(1, 1, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .removeComment(comment);
    }


    @Test
    public void getNumberOfComments_Should_CallRepo() {
        Mockito.when(commentRepository.getNumberOfComments())
                .thenReturn(null);

        service.getNumberOfComments();

        Mockito.verify(commentRepository, Mockito.times(1))
                .getNumberOfComments();
    }

}
