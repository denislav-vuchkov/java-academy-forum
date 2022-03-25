package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                      Optional<String> sortBy, Optional<String> order, Optional<String> limit);

    List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                      Optional<String> sortBy, Optional<String> order, Optional<String> limit, User user);

    List<Post> getMostCommented();

    List<Post> getMostLiked();

    List<Post> getMostRecent();

    Post getById(int id, User user);

    void create(Post post, User user);

    Post update(Post post, User user);

    Post delete(int id, User user);

    Long getNumberOfPosts();

}
