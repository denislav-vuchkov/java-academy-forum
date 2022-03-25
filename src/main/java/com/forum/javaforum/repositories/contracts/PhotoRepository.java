package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.Photo;

public interface PhotoRepository {
    void save(Photo photo);

    void update(Photo photo);

    void delete(Photo photo);
}
