package com.forum.javaforum.models.publication;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@MappedSuperclass
public abstract class PublicationDto {

    @NotNull(message = "Posts and comments must have a content.")
    @Size(min = 32, max = 8192, message = "The content must be between 32 symbols and 8192 symbols.")
    private String content;

    public PublicationDto(String content) {
        this.content = content;
    }

    public PublicationDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

