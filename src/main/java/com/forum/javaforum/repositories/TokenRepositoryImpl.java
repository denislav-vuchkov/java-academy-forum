package com.forum.javaforum.repositories;

import com.forum.javaforum.models.users.ConfirmationToken;
import com.forum.javaforum.repositories.contracts.TokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl implements TokenRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<ConfirmationToken> findByToken(String token) {
        List<ConfirmationToken> result;
        try (Session session = sessionFactory.openSession()) {
            Query<ConfirmationToken> query = session.createQuery(" from ConfirmationToken" +
                    " where token = :token", ConfirmationToken.class);
            query.setParameter("token", token);
            result = query.list();
        }

        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    @Override
    public void save(ConfirmationToken token) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(token);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(ConfirmationToken token) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(token);
            session.getTransaction().commit();
        }
    }
}
