package com.forum.javaforum.repositories;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.models.users.PhoneNumber;
import com.forum.javaforum.repositories.contracts.PhoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhoneRepositoryImpl implements PhoneRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PhoneRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(PhoneNumber number) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(number);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(PhoneNumber number) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(number);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(PhoneNumber number) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(number);
            session.getTransaction().commit();
        }
    }


    public void validatePhoneIsUnique(String value) {
        try (Session session = sessionFactory.openSession()) {
            Query<PhoneNumber> query = session.createQuery(" from PhoneNumber where number = :number", PhoneNumber.class);
            query.setParameter("number", value);

            List<PhoneNumber> result = query.list();
            if (!result.isEmpty()) {
                throw new DuplicateEntityException("User", "phone number", value);
            }
        }
    }



}
