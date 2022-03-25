package com.forum.javaforum.models.users;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "photo")
    private String photo;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private User user;

    public Photo() {
    }

    public Photo(String photo) {
        this.photo = photo;
    }

    public Photo(int userId, String photo) {
        this.userId = userId;
        this.photo = photo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo that = (Photo) o;
        return userId == that.userId && Objects.equals(photo, that.photo) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, photo, user);
    }
}
