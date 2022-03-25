package com.forum.javaforum.models.publication;

public class CommentFilterDto {

    private String author;
    private Integer postID;
    private String keyword;
    private String sortBy;
    private String order;

    public CommentFilterDto(String author, Integer postID, String keyword, String sortBy, String order) {
        this.author = author;
        this.postID = postID;
        this.keyword = keyword;
        this.sortBy = sortBy;
        this.order = order;
    }

    public CommentFilterDto() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
