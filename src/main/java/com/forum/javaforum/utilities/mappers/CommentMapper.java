package com.forum.javaforum.utilities.mappers;

import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    @Autowired
    public CommentMapper() {
    }

    public Comment fromDto(CommentDto commentDto, int postID, User user) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAuthor(user);
        comment.setPostID(postID);
        comment.setDate(LocalDateTime.now());
        return comment;
    }

    public Comment fromDto(Comment comment, CommentDto commentDto) {
        comment.setContent(commentDto.getContent());
        return comment;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        return commentDto;
    }
}
