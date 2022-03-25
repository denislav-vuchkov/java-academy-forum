package com.forum.javaforum.repositories.outdated;

import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
public class UserRepositoryJdbcImpl {
/*
    public static final String GET_ALL = "select u.*, p.photo as photo, pn.phone_number as phone_number " +
            "from users as u" +
            "    left join photos p on u.id = p.user_id" +
            "    left join phone_numbers pn on u.id = pn.user_id";

    private static final String POST = "INSERT INTO users(username, first_name, " +
            "last_name, password, email, registration_date) VALUES(?, ?, ?, ?, ?, ?)";

    private static final String PUT = "UPDATE users SET first_name = ?, " +
            "last_name = ?, password = ?, email = ? WHERE id = ?";

    private static final String POST_AS_DELETED = "UPDATE users SET isDeleted = true WHERE id = ?";

    public static final String PERMITTED_SORT_CRITERIA = "username, id, first_name, last_name, email, registration_date";

    private static final String POST_INTO_PHOTOS = "INSERT INTO photos(user_id, photo) VALUES(?, ?)";

    private static final String QUERY_PHOTOS = "SELECT * FROM photos WHERE user_id = ?";

    private static final String UPDATE_PHOTO = "UPDATE photos SET photo = ? WHERE user_id = ?";

    public static final String ADD_PHONE_NUMBER = "INSERT INTO phone_numbers(user_id, phone_number) VALUES (?, ?)";

    public static final String DELETE_PHONE_NUMBER = "DELETE FROM phone_numbers WHERE user_id = ?";

    public static final String UPDATE_PHONE_NUMBER = "UPDATE phone_numbers SET phone_number = ? WHERE user_id = ?";

    private final String URL, USER, PASS;

    @Autowired
    public UserRepositoryJdbcImpl(Environment env) {
        URL = env.getProperty("db.url");
        USER = env.getProperty("db.user");
        PASS = env.getProperty("db.pass");
    }

    @Override
    public List<User> getAll(String username, String firstName, String email, String sortBy, String sortOrder) {
        List<User> userList;

        String query = GET_ALL;
        query = buildSQLSearch(query, "username", username);
        query = buildSQLSearch(query, "first_name", firstName);
        query = buildSQLSearch(query, "email", email);
        query = addSortCriteria(query, sortBy, sortOrder);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

                userList = getAllHelper(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (query.contains("WHERE") && userList.isEmpty()) {
            throw new EntityNotFoundException("users", "search parameters provided");
        } else if (userList.isEmpty()) {
            throw new EntityNotFoundException("users");
        }

        return userList;
    }

    @Override
    public String buildSQLSearch(String query, String column, String value) {
        if (value == null || value.isEmpty()) return query;

        String addToQuery;

        if (query.contains("WHERE")) {
            addToQuery = String.format(" AND %s = '%s'", column, value);
        } else {
            addToQuery = String.format(" WHERE %s = '%s'", column, value);
        }

        return query + addToQuery;
    }

    @Override
    public String addSortCriteria(String query, String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isEmpty()) {
            return query;
        }

        String addToQuery;

        if (!PERMITTED_SORT_CRITERIA.contains(sortBy)) {
            throw new RuntimeException(String.format("The sort criteria chosen is not a valid one. Please " +
                    "enter one of the following criteria: %s.", PERMITTED_SORT_CRITERIA));
        }
        addToQuery = String.format(" ORDER BY %s", sortBy);

        if (sortOrder == null || sortOrder.isEmpty()) {
            return query + addToQuery;
        } else if (sortOrder.equalsIgnoreCase("desc")) {
            addToQuery += " DESC";
        } else {
            throw new RuntimeException("Invalid sort order has been selected. You can only select a 'desc' " +
                    "sort order. If nothing is selected results will be returned in ascending order.");
        }

        return query + addToQuery;
    }

    @Override
    public User getByID(int id) {
        String getByID = GET_ALL + " WHERE u.id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(getByID)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            List<User> result = getAllHelper(resultSet);

            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "ID", String.valueOf(id));
            } else {
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User getByUsername(String username) {
        String getByUsername = GET_ALL + " WHERE u.username = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(getByUsername)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            List<User> result = getAllHelper(resultSet);
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            } else {
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User createUser(User user) {
        validateParameterIsUnique("users", "username", user.getUsername());
        validateParameterIsUnique("users", "email", user.getEmail());

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement createUserStatement = connection.prepareStatement(POST);
             PreparedStatement addPhotoStatement = connection.prepareStatement(POST_INTO_PHOTOS)) {

            createUserStatement.setString(1, user.getUsername());
            createUserStatement.setString(2, user.getFirst_Name());
            createUserStatement.setString(3, user.getLast_Name());
            createUserStatement.setString(4, user.getPassword());
            createUserStatement.setString(5, user.getEmail());
            createUserStatement.setString(6, user.getRegistrationTime().toString());

            createUserStatement.executeQuery();

            User targetUser = getByUsername(user.getUsername());

            if (user.getPhoto() != null) {
                addPhotoStatement.setInt(1, targetUser.getId());
                addPhotoStatement.setBlob(2, user.getPhoto());

                addPhotoStatement.executeQuery();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return getByUsername(user.getUsername());
    }

    public void validateParameterIsUnique(String table, String columnName, String value) {
        String query = String.format("SELECT * FROM %s WHERE %s = ?", table, columnName);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.first()) {
                throw new DuplicateEntityException("User", columnName, value);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User updateUser(int id, User user) {
        User targetUser = getByID(id);
        If user wants to change their email the below check validates the new email is unique
        if (!user.getEmail().equals(targetUser.getEmail())) {
            validateParameterIsUnique("users", "email", user.getEmail());
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(PUT)) {

            statement.setString(1, user.getFirst_Name());
            statement.setString(2, user.getLast_Name());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.setInt(5, id);

            statement.executeQuery();

            if (user.getPhoto() != null) updateUserPhoto(user, targetUser);
            if (targetUser.isAdmin()) updatePhoneNumber(user, targetUser);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return getByID(id);
    }

    @Override
    public User deleteUser(int id) {
        //This operation will check if there is a user with this ID in the system
        User targetUser = getByID(id);

        if (targetUser.isDeleted()) {
            throw new DuplicateEntityException("User has already been deleted. Operation cannot be performed again.");
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(POST_AS_DELETED)) {

                statement.setInt(1, id);
                statement.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return getByID(id);
    }

    public List<User> getAllHelper(ResultSet resultSet) {
        List<User> users = new ArrayList<>();

        try {
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        UtilityMethods.getDate(resultSet, "registration_date"),
                        resultSet.getBlob("photo"),
                        resultSet.getBoolean("isAdmin"),
                        resultSet.getBoolean("isBlocked"),
                        resultSet.getBoolean("isDeleted"),
                        resultSet.getString("phone_number"));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return users;
    }

    public void updateUserPhoto(User user, User targetUser) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statementQueryPhotos = connection.prepareStatement(QUERY_PHOTOS);
             PreparedStatement statementUpdatePhoto = connection.prepareStatement(UPDATE_PHOTO);
             PreparedStatement statementPostPhoto = connection.prepareStatement(POST_INTO_PHOTOS)) {

            statementQueryPhotos.setInt(1, targetUser.getId());
            ResultSet resultSet = statementQueryPhotos.executeQuery();

            if (resultSet.next()) {
                statementUpdatePhoto.setBlob(1, user.getPhoto());
                statementUpdatePhoto.setInt(2, targetUser.getId());

                statementQueryPhotos.executeQuery();
            } else {
                statementPostPhoto.setInt(1, targetUser.getId());
                statementPostPhoto.setBlob(2, user.getPhoto());

                statementQueryPhotos.executeQuery();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e. getMessage());
        }
    }

    private void updatePhoneNumber(User user, User targetUser) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statementDelete = connection.prepareStatement(DELETE_PHONE_NUMBER);
             PreparedStatement statementAdd = connection.prepareStatement(ADD_PHONE_NUMBER);
             PreparedStatement statementUpdate = connection.prepareStatement(UPDATE_PHONE_NUMBER)) {

            if (targetUser.getPhoneNumber() != null && user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                statementDelete.setInt(1, targetUser.getId());
                statementDelete.executeQuery();
            } else if (targetUser.getPhoneNumber() == null && user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                statementAdd.setInt(1, targetUser.getId());
                statementAdd.setString(2, user.getPhoneNumber());
                statementAdd.executeQuery();
            } else {
                statementUpdate.setString(1, user.getPhoneNumber());
                statementUpdate.setInt(2, targetUser.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
*/

}