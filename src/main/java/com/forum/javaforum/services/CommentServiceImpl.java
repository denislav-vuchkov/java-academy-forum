package com.forum.javaforum.services;

import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.CommentRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.utilities.PublicationValidators.*;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepo, PostRepository postRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
    }

    @Override
    public List<Comment> getAll(Optional<String> author,
                                Optional<Integer> postID,
                                Optional<String> keyword,
                                Optional<String> sortBy,
                                Optional<String> order,
                                User user) {
        validatePublicationAccess(user);
        if (sortBy.isEmpty()) sortBy = Optional.empty();
        return commentRepo.getAll(author, postID, keyword, sortBy, order);
    }

    @Override
    public void addComment(Comment comment, User user) {
        validatePostExists(comment.getPostID());
        validatePublicationCreation(user);
        commentRepo.addComment(comment);
    }

    @Override
    public Comment editComment(int postID, int commentID, Comment modified, User user) {
        Comment original = getCommentIfExists(postID, commentID);
        validatePublicationModification(user, original.getAuthor().getId());
        return commentRepo.editComment(modified);
    }

    @Override
    public Comment removeComment(int postID, int commentID, User user) {
        Comment comment = getCommentIfExists(postID, commentID);
        validatePublicationModification(user, comment.getAuthor().getId());
        commentRepo.removeComment(comment);
        return comment;
    }

    @Override
    public Comment getCommentIfExists(int postID, int commentID) {
        validatePostExists(postID);
        Comment comment = commentRepo.getById(commentID);
        if (comment.getPostID() != postID) throw new IllegalArgumentException("Post and comment mismatch.");
        return comment;
    }

    @Override
    public Long getNumberOfComments() {
        return commentRepo.getNumberOfComments();
    }

    private void validatePostExists(int id) {
        postRepo.getById(id);
    }
}
