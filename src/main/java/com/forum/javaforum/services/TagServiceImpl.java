package com.forum.javaforum.services;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.repositories.contracts.TagRepository;
import com.forum.javaforum.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.forum.javaforum.utilities.PublicationValidators.*;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepo;
    private final PostRepository postRepo;

    @Autowired
    public TagServiceImpl(TagRepository tagRepo, PostRepository postRepo) {
        this.tagRepo = tagRepo;
        this.postRepo = postRepo;
    }

    @Override
    public List<Tag> getTags(int postID, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        return tagRepo.getTags(postID);
    }

    @Override
    public Tag addTag(int postID, String tag, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
        validateUserIsAuthorOrAdmin(user, getAuthor(postID));
        validateTagNotDuplicate(postID, tag);
        if (tagRepo.findTag(tag).isEmpty()) tagRepo.createTag(tag);
        return tagRepo.addTag(postID, tag);
    }

    @Override
    public Tag removeTag(int postID, String tag, User user) {
        validatePostExists(postID);
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
        validateUserIsAuthorOrAdmin(user, getAuthor(postID));
        validatePostAlreadyTagged(postID, tag);
        return tagRepo.removeTag(postID, tag);
    }

    private void validatePostExists(int postID) {
        postRepo.getById(postID);
    }

    private int getAuthor(int postID) {
        return postRepo.getById(postID).getAuthor().getId();
    }

    private void validateTagNotDuplicate(int postID, String tag) {
        if (tagRepo.findTag(postID, tag).isPresent())
            throw new DuplicateEntityException("Tag", "name", "'" + tag + "'");
    }

    private void validatePostAlreadyTagged(int postID, String tag) {
        if (tagRepo.findTag(postID, tag).isEmpty())
            throw new EntityNotFoundException("such tag");
    }

}
