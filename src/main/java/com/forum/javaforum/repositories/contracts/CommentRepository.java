package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.publication.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    List<Comment> getAll(Optional<String> author,
                         Optional<Integer> postID,
                         Optional<String> keyword,
                         Optional<String> sortBy,
                         Optional<String> order);

    Comment getById(int id);

    void addComment(Comment comment);

    Comment editComment(Comment comment);

    void removeComment(Comment comment);

    Long getNumberOfComments();

}
