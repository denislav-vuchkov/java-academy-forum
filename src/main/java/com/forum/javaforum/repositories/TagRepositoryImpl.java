package com.forum.javaforum.repositories;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.repositories.contracts.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@PropertySource("classpath:application.properties")
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;
    private final PostRepository postRepo;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory, PostRepository postRepo) {
        this.sessionFactory = sessionFactory;
        this.postRepo = postRepo;
    }

    @Override
    public List<Tag> getTags(int postID) {
        return new ArrayList<>(postRepo.getById(postID).getTags());
    }

    @Override
    public Optional<Tag> findTag(int postID, String tagName) {
        return postRepo.getById(postID).getTags().stream().filter(entry -> entry.getName().equals(tagName)).findAny();
    }

    @Override
    public Tag addTag(int postID, String tagName) {
        Post post = postRepo.getById(postID);
        Tag tag = findTag(tagName).get();
        post.getTags().add(tag);
        postRepo.update(post);
        return tag;
    }

    @Override
    public Tag removeTag(int postID, String tagName) {
        Post post = postRepo.getById(postID);
        Tag tag = findTag(tagName).get();
        post.getTags().remove(tag);
        postRepo.update(post);
        return tag;
    }

    @Override
    public Optional<Tag> findTag(String tagName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("from Tag where name = :tagName", Tag.class);
            query.setParameter("tagName", tagName);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public void createTag(String tagName) {
        try (Session session = sessionFactory.openSession()) {
            session.save(new Tag(0, tagName));
        }
    }
}
