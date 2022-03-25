package com.forum.javaforum.repositories;

import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.AdminRepository;
import com.forum.javaforum.repositories.contracts.UserRepository;
import com.forum.javaforum.utilities.UsersQueryMaker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepositoryHibernateImpl implements AdminRepository {

    private final UserRepository userRepository;
    private final SessionFactory sessionFactory;

    @Autowired
    public AdminRepositoryHibernateImpl(SessionFactory sessionFactory, UserRepository userRepository) {
        this.sessionFactory = sessionFactory;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll(Optional<String> username, Optional<String> firstName, Optional<String> email,
                             Optional<String> sortBy, Optional<String> sortOrder) {
        try (Session session = sessionFactory.openSession()) {

            UsersQueryMaker queryMaker = new UsersQueryMaker(" from User where isAdmin = true ");
            String query = queryMaker.buildHQLSearchAndSortQuery(username, firstName, email, sortBy, sortOrder);
            HashMap<String, Object> propertiesMap = queryMaker.getProperties();

            Query<User> request = session.createQuery(query.toString(), User.class);
            request.setProperties(propertiesMap);

            return request.list();
        }
    }

    @Override
    public List<User> getAllNonAdmins(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> nonAdmins = session.createQuery(" from User where isAdmin = false" +
                    " and isDeleted = false and isBlocked = false and isEnabled = true" +
                    " and username like :username order by username", User.class);
            nonAdmins.setParameter("username", "%" + username + "%");

            return nonAdmins.list();
        }
    }

    @Override
    public List<User> getAllBlockedUsers(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> blockedUsers = session.createQuery(" from User where isBlocked = true " +
                    "and isDeleted = false and isAdmin = false and isEnabled = true and" +
                    " username like :username order by username", User.class);
            blockedUsers.setParameter("username", "%" + username + "%");

            return blockedUsers.list();
        }
    }

    @Override
    public List<User> getAllUnblockedUsers(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> unblockedUsers = session.createQuery(" from User where isBlocked = false" +
                    " and isDeleted = false and isAdmin = false and isEnabled = true " +
                    " and username like :username order by username", User.class);
            unblockedUsers.setParameter("username", "%" + username + "%");


            return unblockedUsers.list();
        }
    }

    @Override
    public List<User> getAllNonDeletedUsers(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> nonDeletedUsers = session.createQuery(" from User where isDeleted = false " +
                    " and isAdmin = false and isEnabled = true and username like :username " +
                    " order by username", User.class);
            nonDeletedUsers.setParameter("username", "%" + username + "%");

            return nonDeletedUsers.list();
        }
    }

    @Override
    public User getById(int id) {
        User targetUser = userRepository.getByID(id);

        if (targetUser.isAdmin()) {
            return targetUser;
        } else {
            throw new IllegalArgumentException(String.format("User with id %d is not an admin.", id));
        }
    }

    @Override
    public User getByUsername(String username) {
        User targetUser = userRepository.getByUsername(username);

        if (targetUser.isAdmin()) {
            return targetUser;
        } else {
            throw new IllegalArgumentException(String.format("User with username '%s' is not an admin.", username));
        }
    }

    @Override
    public User makeAdmin(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            targetUser.setAdmin(true);

            session.beginTransaction();
            session.update(targetUser);
            session.getTransaction().commit();
        }

        return getById(targetUser.getId());
    }

    @Override
    public User removeAdmin(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            targetUser.setAdmin(false);

            session.beginTransaction();
            session.update(targetUser);
            session.getTransaction().commit();
        }

        return userRepository.getByID(targetUser.getId());
    }

    @Override
    public User blockUser(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            targetUser.setBlocked(true);

            session.beginTransaction();
            session.update(targetUser);
            session.getTransaction().commit();
        }

        return userRepository.getByID(targetUser.getId());
    }

    @Override
    public User unblockUser(User targetUser) {
        try (Session session = sessionFactory.openSession()) {
            targetUser.setBlocked(false);

            session.beginTransaction();
            session.update(targetUser);
            session.getTransaction().commit();
        }

        return userRepository.getByID(targetUser.getId());
    }
}
