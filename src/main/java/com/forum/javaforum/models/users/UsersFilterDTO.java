package com.forum.javaforum.models.users;

public class UsersFilterDTO {

    private String username;
    private String firstName;
    private String email;
    private String sortBy;
    private String sortOrder;

    public UsersFilterDTO() {
    }

    public UsersFilterDTO(String username, String firstName, String email, String sortBy, String sortOrder) {
        this.username = username;
        this.firstName = firstName;
        this.email = email;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
