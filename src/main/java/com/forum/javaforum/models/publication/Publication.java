package com.forum.javaforum.models.publication;

import com.forum.javaforum.models.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "content")
    private String content;
    @Column(name = "date")
    private LocalDateTime date;

    public Publication(int id, User author, String content, LocalDateTime date) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public Publication() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
