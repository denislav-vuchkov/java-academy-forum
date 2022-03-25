package com.forum.javaforum.models.users;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "registration_date")
    private LocalDateTime registrationTime;

    @OneToOne(mappedBy = "user")
    private Photo photo;

    @Column(name = "isAdmin")
    boolean isAdmin;

    @Column(name = "isBlocked")
    boolean isBlocked;

    @Column(name = "isDeleted")
    boolean isDeleted;

    @Column(name = "isEnabled")
    boolean isEnabled;

    @OneToOne(mappedBy = "user")
    private PhoneNumber phoneNumber;

    public User() {
    }

    public User(int id, String username, String firstName, String lastName,
                String password, String email, LocalDateTime registrationTime, Photo photo,
                boolean isAdmin, boolean isBlocked, boolean isDeleted, PhoneNumber phoneNumber, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.registrationTime = registrationTime;
        this.photo = photo;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.isEnabled = isEnabled;
        this.phoneNumber = phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_Name) {
        this.firstName = first_Name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_Name) {
        this.lastName = last_Name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Photo getPhoto() {
        return photo;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username)
                && registrationTime.equals(user.registrationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, registrationTime);
    }
}
