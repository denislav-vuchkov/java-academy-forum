package com.forum.javaforum.repositories.outdated;

import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
public class AdminRepositoryJdbcImpl {

    public static final String MAKE_ADMIN = "UPDATE users SET isAdmin = true WHERE id = ?";
    public static final String REMOVE_ADMIN = "UPDATE users SET isAdmin = false WHERE id = ?";
    public static final String BLOCK_USER = "UPDATE users SET isBlocked = true WHERE id = ?";
    public static final String UNBLOCK_USER = "UPDATE users SET isBlocked = false WHERE id = ?";

//    private final String URL, USER, PASS;
//    private final UserRepository userRepository;

//    @Autowired
//    public AdminRepositoryImpl(Environment env, UserRepository userRepository) {
//        URL = env.getProperty("db.url");
//        USER = env.getProperty("db.user");
//        PASS = env.getProperty("db.pass");
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public List<User> getAll(String username, String firstName, String email, String sortBy, String sortOrder) {
//        List<User> adminsList = new ArrayList<>();
////
////        String query = String.format("%s\n WHERE isAdmin = true", GET_ALL);
////        query = userRepository.buildSQLSearch(query,"username", username);
////        query = userRepository.buildSQLSearch(query,"first_name", firstName);
////        query = userRepository.buildSQLSearch(query,"email", email);
////        query = userRepository.addSortCriteria(query, sortBy, sortOrder);
////
////        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
////            try (Statement statement = connection.createStatement()) {
////                try (ResultSet resultSet = statement.executeQuery(query)) {
////                    adminsList = userRepository.getAllHelper(resultSet);
////                }
////            }
////        } catch (SQLException e) {
////            throw new RuntimeException(e.getMessage());
////        }
////
////        if (query.contains("AND") && adminsList.isEmpty()) {
////            throw new EntityNotFoundException("admins", "search parameters provided");
////        } else if (adminsList.isEmpty()) {
////            throw new EntityNotFoundException("admins");
////        }
//
//        return adminsList;
//    }
//
//    @Override
//    public User getById(int id) {
//        User targetUser = userRepository.getByID(id);
//
//        if (targetUser.isAdmin()) {
//            return targetUser;
//        } else {
//            throw new RuntimeException(String.format("User with id %d is not an admin.", id));
//        }
//    }
//
//    @Override
//    public User getByUsername(String username) {
//        User targetUser = userRepository.getByUsername(username);
//
//        if (targetUser.isAdmin()) {
//            return targetUser;
//        } else {
//            throw new RuntimeException(String.format("User with username '%s' is not an admin.", username));
//        }
//    }
//
//    @Override
//    public User makeAdmin(int id) {
//        //Below row validate user exists
//        User targetUser = userRepository.getByID(id);
//
//        if (targetUser.isAdmin()) {
//            throw new DuplicateEntityException(String.format("User with id %d is already an admin.", id));
//        }
//
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
//             PreparedStatement statement = connection.prepareStatement(MAKE_ADMIN)) {
//
//            statement.setInt(1, id);
//            statement.executeQuery();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//        return getById(id);
//    }
//
//    @Override
//    public User removeAdmin(int id) {
//        //Below method will throw exception if user is not an admin
//        User targetUser = getById(id);
////
////        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
////             PreparedStatement statementPhoneNumber = connection.prepareStatement(DELETE_PHONE_NUMBER);
////             PreparedStatement statementRemoveAdmin = connection.prepareStatement(REMOVE_ADMIN)) {
////
////            statementPhoneNumber.setInt(1, id);
////            statementPhoneNumber.executeQuery();
////
////            statementRemoveAdmin.setInt(1, id);
////            statementRemoveAdmin.executeQuery();
////
////        } catch (SQLException e) {
////            throw new RuntimeException(e.getMessage());
////        }
//
//        return userRepository.getByID(id);
//    }
//
//    @Override
//    public User blockUser(int id) {
//        User targetUser = userRepository.getByID(id);
//
//        if (targetUser.isBlocked()) {
//            throw new DuplicateEntityException(String.format("User with id %d is already blocked.", id));
//        }
//
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
//             PreparedStatement statement = connection.prepareStatement(BLOCK_USER)) {
//
//            statement.setInt(1, id);
//            statement.executeQuery();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//        return userRepository.getByID(id);
//    }
//
//    @Override
//    public User unblockUser(int id) {
//        User targetUser = userRepository.getByID(id);
//
//        if (!targetUser.isBlocked()) {
//            throw new DuplicateEntityException(String.format("User with id %d is not blocked " +
//                    "so cannot be unblocked.", id));
//        }
//
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
//             PreparedStatement statement = connection.prepareStatement(UNBLOCK_USER)) {
//
//            statement.setInt(1, id);
//            statement.executeQuery();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//        return userRepository.getByID(id);
//    }
}
