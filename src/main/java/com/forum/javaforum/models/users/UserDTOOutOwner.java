package com.forum.javaforum.models.users;

import java.time.LocalDateTime;

public class UserDTOOutOwner implements UserDTOOutInterface {

    private int id;
    private String username;
    private String first_Name;
    private String last_Name;
    private String password;
    private String email;
    private LocalDateTime registrationTime;
    private Photo photo;
    private boolean isAdmin ;
    private boolean isBlocked;
    private boolean isDeleted;
    private boolean isEnabled;
    private PhoneNumber phoneNumber;


    public UserDTOOutOwner(int id, String username, String first_Name, String last_Name, String password,
                           String email, LocalDateTime registrationTime, Photo photo, boolean isAdmin,
                           boolean isBlocked, boolean isDeleted, PhoneNumber phoneNumber, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.password = password;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
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

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isDeleted() {
        return isDeleted;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
