package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.publication.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    List<Tag> getTags(int postID);

    Optional<Tag> findTag(int postID, String tagName);

    Tag addTag(int postID, String tagName);

    Tag removeTag(int postID, String tagName);

    Optional<Tag> findTag(String tagName);

    void createTag(String tagName);

}
