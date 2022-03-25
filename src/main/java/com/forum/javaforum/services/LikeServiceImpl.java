package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.LikeRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.services.contracts.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.forum.javaforum.utilities.PublicationValidators.validUserIsNotBlocked;
import static com.forum.javaforum.utilities.PublicationValidators.validateUserIsNotDeleted;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepo;
    private final PostRepository postRepo;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepo, PostRepository postRepo) {
        this.likeRepo = likeRepo;
        this.postRepo = postRepo;
    }

    @Override
    public List<User> getLikes(int postID, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        return likeRepo.getLikes(postID);
    }

    @Override
    public User addLike(int postID, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
        validateLikeNotDuplicate(postID, user);
        return likeRepo.addLike(postID, user.getId());
    }

    @Override
    public User removeLike(int postID, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
        validatePostAlreadyLiked(postID, user);
        return likeRepo.removeLike(postID, user.getId());
    }

    private void validatePostExists(int postID) {
        postRepo.getById(postID);
    }

    private void validateLikeNotDuplicate(int postID, User user) {
        if (likeRepo.findLike(postID, user.getId()).isPresent())
            throw new DuplicateEntityException("User already liked this post.");
    }

    private void validatePostAlreadyLiked(int postID, User user) {
        if (likeRepo.findLike(postID, user.getId()).isEmpty())
            throw new EntityNotFoundException("like");
    }

}
