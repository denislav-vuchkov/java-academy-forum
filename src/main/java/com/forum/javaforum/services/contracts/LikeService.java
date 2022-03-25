package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.User;

import java.util.List;

public interface LikeService {

    List<User> getLikes(int postID, User user);

    User addLike(int postID, User user);

    User removeLike(int postID, User user);

}
