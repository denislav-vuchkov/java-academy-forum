package com.forum.javaforum.repositories;

import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.repositories.contracts.CommentRepository;
import com.forum.javaforum.utilities.CommentQueryMaker;
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
public class CommentRepositoryImpl implements CommentRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Comment> getAll(Optional<String> author,
                                Optional<Integer> postID,
                                Optional<String> keyword,
                                Optional<String> sortBy,
                                Optional<String> order) {

        CommentQueryMaker queryMaker = new CommentQueryMaker();
        queryMaker.setFilterClause(author, postID, keyword);
        queryMaker.setSortClause(sortBy, order);

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Comment> results = session.createSQLQuery(queryMaker.getQuery());
            results.addEntity(Comment.class);
            results.setProperties(queryMaker.getParams());
            return results.list();
        }
    }

    @Override
    public Comment getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("from Comment where id = :id", Comment.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException("Comment", "ID", String.valueOf(id)));
        }
    }

    @Override
    public void addComment(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.save(comment);
        }
    }

    @Override
    public Comment editComment(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(comment);
            session.getTransaction().commit();
        }
        return getById(comment.getId());
    }

    @Override
    public void removeComment(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public Long getNumberOfComments() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from Comment", Long.class);
            return query.getSingleResult();
        }
    }
}
