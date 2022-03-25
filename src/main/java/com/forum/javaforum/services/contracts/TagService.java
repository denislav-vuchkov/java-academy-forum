package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;

import java.util.List;

public interface TagService {

    List<Tag> getTags(int postID, User user);

    Tag addTag(int postID, String tag, User user);

    Tag removeTag(int postID, String tag, User user);

}
