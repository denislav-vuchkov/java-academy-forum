package com.forum.javaforum.models.publication;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.forum.javaforum.models.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "posts")
@JsonIgnoreProperties(value = {"comments", "likes"})
public class Post extends Publication {

    @Column(name = "title")
    private String title;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Set<Comment> comments;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "posts_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likes;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "posts_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    public Post(int id, User author, String title, String content, LocalDateTime date) {
        super(id, author, content, date);
        this.title = title;
        comments = new HashSet<>();
        likes = new HashSet<>();
        tags = new HashSet<>();
    }

    public Post() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    @JsonIgnore
    public Set<Comment> getSortedComments() {
        return comments.stream().
                sorted(Comparator.comparing(Comment::getDate)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<User> getLikes() {
        return likes;
    }

    @JsonIgnore
    public Set<User> getSortedLikes() {
        return likes.stream().
                sorted(Comparator.comparing(User::getUsername)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    @JsonIgnore
    public Set<Tag> getSortedTags() {
        return tags.stream().
                sorted(Comparator.comparing(Tag::getName)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty
    public int numberOfComments() {
        return comments == null ? 0 : comments.size();
    }

    @JsonProperty
    public int numberOfLikes() {
        return likes == null ? 0 : likes.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return (super.getId() == post.getId() && super.getAuthor() == post.getAuthor() && super.getDate().isEqual(post.getDate()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId(), super.getAuthor(), super.getDate());
    }
}
