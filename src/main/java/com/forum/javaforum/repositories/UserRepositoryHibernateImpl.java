package com.forum.javaforum.repositories;

import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.models.users.LoginDTO;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.UserRepository;
import com.forum.javaforum.exceptions.EntityNotFoundException;
import com.forum.javaforum.utilities.UsersQueryMaker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryHibernateImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryHibernateImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email,
                             Optional<String> sortBy, Optional<String> sortOrder) {
        try (Session session = sessionFactory.openSession()) {

            UsersQueryMaker queryMaker = new UsersQueryMaker(" from User ");
            String query = queryMaker.buildHQLSearchAndSortQuery(username, firstName, email, sortBy, sortOrder);
            HashMap<String, Object> propertiesMap = queryMaker.getProperties();

            Query<User> request = session.createQuery(query, User.class);
            request.setProperties(propertiesMap);

            return request.list();
        }
    }

    @Override
    public User getByID(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", "ID", String.valueOf(id));
            }
            return user;
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> result = query.list();

            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }

            return result.get(0);
        }
    }

    @Override
    public User save(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.save(user);
        }

        return getByID(user.getId());
    }

    @Override
    public User update(int id, User updatedUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(updatedUser);
            session.getTransaction().commit();
        }

        return getByID(id);
    }

    @Override
    public User delete(User user) {
        user.setDeleted(true);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        }

        return getByID(user.getId());
    }

    @Override
    public void validateUserParamIsUnique(String column, String value) {
        String queryString = String.format(" from User where %s = :%s ", column, column);
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(queryString, User.class);
            query.setParameter(column, value);

            List<User> result = query.list();
            if (!result.isEmpty()) {
                throw new DuplicateEntityException("User", column, value);
            }
        }
    }

    @Override
    public Long getNumberOfUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(" select count(user) from User as user", Long.class);

            return query.getSingleResult();
        }
    }

    @Override
    public Long getNumberOfInactiveUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(" select count(user) from User as user" +
                    " where isBlocked =  true or isDeleted = true ", Long.class);

            return query.getSingleResult();
        }
    }

    @Override
    public Long getNumberOfAdmins() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(" select count(user) from User as user" +
                    " where isAdmin =  true and isBlocked = false and isDeleted = false ", Long.class);

            return query.getSingleResult();
        }
    }

}