package com.forum.javaforum.repositories;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.LikeRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@PropertySource("classpath:application.properties")
public class LikeRepositoryImpl implements LikeRepository {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @Autowired
    public LikeRepositoryImpl(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getLikes(int postID) {
        return new ArrayList<>(postRepo.getById(postID).getLikes());
    }

    @Override
    public Optional<User> findLike(int postID, int userID) {
        return postRepo.getById(postID).getLikes().stream().filter(user -> user.getId() == userID).findAny();
    }

    @Override
    public User addLike(int postID, int userID) {
        Post post = postRepo.getById(postID);
        User user = userRepo.getByID(userID);
        post.getLikes().add(user);
        postRepo.update(post);
        return user;
    }

    @Override
    public User removeLike(int postID, int userID) {
        Post post = postRepo.getById(postID);
        User user = userRepo.getByID(userID);
        post.getLikes().remove(user);
        postRepo.update(post);
        return user;
    }
}
