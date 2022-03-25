package com.forum.javaforum.models.publication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.forum.javaforum.models.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
@JsonIgnoreProperties(value = {"id"}, allowGetters = true)
public class Comment extends Publication {

    @Column(name = "post_id")
    private int postID;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    public Comment(int id, User author, int postID, String content, LocalDateTime date) {
        super(id, author, content, date);
        this.postID = postID;
    }

    public Comment() {
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return (super.getId() == comment.getId() && super.getAuthor() == comment.getAuthor() && postID == comment.postID && super.getDate().isEqual(comment.getDate()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId(), super.getAuthor(), postID, super.getDate());
    }
}
