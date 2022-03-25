package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    List<User> getLikes(int postID);

    Optional<User> findLike(int postID, int userID);

    User addLike(int postID, int userID);

    User removeLike(int postID, int userID);

}
