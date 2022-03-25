package com.forum.javaforum.models.users;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "phone_numbers")
public class PhoneNumber {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "phone_number")
    private String number;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private User user;

    public PhoneNumber() {
    }

    public PhoneNumber(String number) {
        this.number = number;
    }

    public PhoneNumber(int userId, String number) {
        this.userId = userId;
        this.number = number;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String phoneNumber) {
        this.number = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return userId == that.userId && Objects.equals(number, that.number) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, number, user);
    }
}
