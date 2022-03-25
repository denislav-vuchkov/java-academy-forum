package com.forum.javaforum.repositories;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.repositories.contracts.PostRepository;
import com.forum.javaforum.repositories.contracts.UserRepository;
import com.forum.javaforum.utilities.PostQueryMaker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@PropertySource("classpath:application.properties")
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Post> getAll(Optional<String> author, Optional<String> keyword, Optional<String> tag,
                             Optional<String> sortBy, Optional<String> order, Optional<Long> limit) {

        PostQueryMaker queryMaker = new PostQueryMaker();
        queryMaker.setFilterClause(author, keyword, tag);
        queryMaker.setSortClause(sortBy, order, limit);

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Post> results = session.createSQLQuery(queryMaker.getQuery());
            results.addEntity(Post.class);
            results.setProperties(queryMaker.getParams());
            return results.list();
        }
    }

    @Override
    public Post getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) throw new EntityNotFoundException("Post", "ID", String.valueOf(id));
            return post;
        }
    }

    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.save(post);
        }
    }

    @Override
    public Post update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(post);
            session.getTransaction().commit();
        }
        return getById(post.getId());
    }

    @Override
    public void delete(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public Long getNumberOfPosts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from Post", Long.class);
            return query.getSingleResult();
        }
    }
}
