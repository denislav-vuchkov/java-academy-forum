package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<Comment> getAll(Optional<String> author,
                         Optional<Integer> postID,
                         Optional<String> keyword,
                         Optional<String> sortBy,
                         Optional<String> order,
                         User user);

    void addComment(Comment comment, User user);

    Comment editComment(int postID, int commentID, Comment comment, User user);

    Comment removeComment(int postID, int commentID, User user);

    Comment getCommentIfExists(int postID, int commentID);

    Long getNumberOfComments();

}
