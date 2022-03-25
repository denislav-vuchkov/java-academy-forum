package com.forum.javaforum.models.users;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDTOInImpl implements UserDTOIn {

    @NotNull
    @Size(min=4, max=32, message="Username must be between 4 and 32 symbols!")
    public String username;

    @NotNull
    @Size(min=4, max=32, message="First name must be between 4 and 32 symbols!")
    public String firstName;

    @NotNull
    @Size(min=4, max=32, message="Last name must be between 4 and 32 symbols!")
    public String lastName;

    @NotNull
    @Pattern(message = "Minimum eight characters, at least one uppercase letter," +
            " one lowercase letter, one number and one special character:",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    public String password;

    @NotNull
    @Email(message = "Please enter a valid email address.",
            regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
    public String email;

    public String phoneNumber;

    public UserDTOInImpl() {

    }

    public UserDTOInImpl(String username, String first_Name, String lastName,
                         String password, String email, String phoneNumber) {
        this.username = username;
        this.firstName = first_Name;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
