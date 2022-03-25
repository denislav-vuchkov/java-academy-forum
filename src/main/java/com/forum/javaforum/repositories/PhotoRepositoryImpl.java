package com.forum.javaforum.repositories;

import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.repositories.contracts.PhotoRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoRepositoryImpl implements PhotoRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PhotoRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Photo photo) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(photo);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Photo photo) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(photo);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Photo photo) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(photo);
            session.getTransaction().commit();
        }
    }

}
