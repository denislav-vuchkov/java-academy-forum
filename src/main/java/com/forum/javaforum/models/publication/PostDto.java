package com.forum.javaforum.models.publication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(value = {"tag1", "tag2", "tag3", "tag4", "tag5"})
public class PostDto extends PublicationDto {

    private static final String VALID_TAG = "(^$)|(^([\\w-]{2,50})+$)";
    private static final String TAG_WARNING = "Tags must be between 2 and 50 symbols and can contain only letters, digits and hyphen. ";

    @NotNull(message = "Posts must have a title.")
    @Size(min = 16, max = 64, message = "The title must be between 16 and 64 symbols.")
    private String title;
    private List<String> tags;

    @Pattern(regexp = VALID_TAG, message = TAG_WARNING)
    private String tag1;
    @Pattern(regexp = VALID_TAG, message = TAG_WARNING)
    private String tag2;
    @Pattern(regexp = VALID_TAG, message = TAG_WARNING)
    private String tag3;
    @Pattern(regexp = VALID_TAG, message = TAG_WARNING)
    private String tag4;
    @Pattern(regexp = VALID_TAG, message = TAG_WARNING)
    private String tag5;

    public PostDto(String title, String content) {
        super(content);
        this.title = title;
    }

    public PostDto(String content, String title, List<String> tags) {
        super(content);
        this.title = title;
        this.tags = tags;
    }

    public PostDto(String content, String title,
                   String tag1, String tag2, String tag3, String tag4, String tag5) {
        this(content, title);
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
    }

    public PostDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTag4() {
        return tag4;
    }

    public void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    public String getTag5() {
        return tag5;
    }

    public void setTag5(String tag5) {
        this.tag5 = tag5;
    }
}
