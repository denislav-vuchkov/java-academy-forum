package com.forum.javaforum.models.users;

import java.time.LocalDateTime;

public class UserDTOOut implements UserDTOOutInterface {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registrationTime;
    private Photo photo;
    private boolean isAdmin ;
    private boolean isBlocked;
    private boolean isDeleted;
    private boolean isEnabled;
    private PhoneNumber phoneNumber;

    public UserDTOOut(int id, String username, String firstName, String lastName,
                      String email, LocalDateTime registrationTime, Photo photo,
                      boolean isAdmin, boolean isBlocked, boolean isDeleted, PhoneNumber phoneNumber, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationTime = registrationTime;
        this.photo = photo;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.phoneNumber = phoneNumber;
        this.isEnabled = isEnabled;
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

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public Photo getPhoto() {
        return photo;
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

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
