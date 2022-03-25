package com.forum.javaforum.models.publication;

import java.util.Optional;

public class PostFilterDto {

    private String author;
    private String keyword;
    private String tag;
    private String sortBy;
    private String order;

    public PostFilterDto(String author, String keyword, String tag, String sortBy, String order) {
        this.author = author;
        this.keyword = keyword;
        this.tag = tag;
        this.sortBy = sortBy;
        this.order = order;
    }

    public PostFilterDto() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
