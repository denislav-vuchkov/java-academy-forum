package com.forum.javaforum.services;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.CommentRepository;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.forum.javaforum.utilities.PublicationValidators.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    @Autowired
    public PostServiceImpl(PostRepository postRepo, CommentRepository commentRepo) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    public List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                             Optional<String> sortBy, Optional<String> order, Optional<String> limit) {
        if (sortBy.isEmpty()) sortBy = Optional.of("date");
        return postRepo.getAll(author, keyword, tag, sortBy, order, parseNumber(limit));
    }

    @Override
    public List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                             Optional<String> sortBy, Optional<String> order, Optional<String> limit, User user) {
        validatePublicationAccess(user);
        return getAll(author, keyword, tag, sortBy, order, limit);
    }

    @Override
    public List<Post> getMostCommented() {
        return getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("comments"), Optional.of("desc"), Optional.of("10"));
    }

    @Override
    public List<Post> getMostLiked() {
        return getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("likes"), Optional.of("desc"), Optional.of("10"));
    }

    @Override
    public List<Post> getMostRecent() {
        return getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("date"), Optional.of("desc"), Optional.of("10"));
    }

    @Override
    public Post getById(int id, User user) {
        validatePublicationAccess(user);
        return postRepo.getById(id);
    }

    @Override
    public void create(Post post, User user) {
        validatePublicationCreation(user);
        postRepo.create(post);
    }

    @Override
    public Post update(Post post, User user) {
        validatePublicationModification(user, getAuthor(post.getId()));
        return postRepo.update(post);
    }

    @Override
    public Post delete(int id, User user) {
        validatePublicationModification(user, getAuthor(id));
        Post post = postRepo.getById(id);
        post.getComments().forEach(commentRepo::removeComment);
        post.getComments().clear();
        post.getLikes().clear();
        post.getTags().clear();
        postRepo.update(post);
        postRepo.delete(post);
        return post;
    }

    @Override
    public Long getNumberOfPosts() {
        return postRepo.getNumberOfPosts();
    }

    private int getAuthor(int postID) {
        return postRepo.getById(postID).getAuthor().getId();
    }

    private Optional<Long> parseNumber(Optional<String> limit) {
        if (limit.isPresent()) {
            try {
                long number = Long.parseLong(limit.get());
                if (number < 1) throw new NumberFormatException();
                return Optional.of(number);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The value for limit must be a whole positive number.");
            }
        }
        return Optional.empty();
    }
}