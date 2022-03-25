package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.publication.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                      Optional<String> sortBy, Optional<String> order, Optional<Long> limit);

    Post getById(int id);

    void create(Post post);

    Post update(Post post);

    void delete(Post post);

    Long getNumberOfPosts();

}
