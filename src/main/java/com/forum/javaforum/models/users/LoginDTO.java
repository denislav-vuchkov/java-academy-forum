package com.forum.javaforum.models.users;

import javax.validation.constraints.NotNull;

public class LoginDTO {

    @NotNull(message = "Please enter a username.")
    private String username;


    @NotNull(message = "Please enter a password.")
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
