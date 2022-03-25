package com.forum.javaforum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.javaforum.models.publication.Comment;
import com.forum.javaforum.models.publication.CommentDto;
import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.PostDto;
import com.forum.javaforum.models.users.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class HelperForTisho {

    public static PostDto createPostDTOIn() {
        PostDto post = new PostDto();

        post.setTitle("How can we use constraint in every element");
        post.setContent("Why is my forall loop not working to apply an constraint to all the individual values stored in my variable. For eg: if there are 100 values stored under a variable k, how can I apply the constraint where each value is less than a certain constant.");

        return post;
    }

    public static Post createPostWithComments() {
        Post post = new Post();

        post.setTitle("How can we use constraint in every element");
        post.setContent("Why is my forall loop not working to apply an constraint to all the individual values stored in my variable. For eg: if there are 100 values stored under a variable k, how can I apply the constraint where each value is less than a certain constant.");
        post.setAuthor(createUserAdmin());
        post.setId(1);
        post.setDate(LocalDateTime.now());

        Set<Comment> commentList = new HashSet<>();

        Comment firstComm = new Comment();
        firstComm.setId(1);
        firstComm.setAuthor(createUserNormal());
        firstComm.setPostID(1);
        firstComm.setContent("Emili, imah absoljutno s\"shhata drama(na 1va)- nakraja prosto zamenih proverkata za naj- goljama stojnost, kojato bjah napravila s preobrazuvane na mapa v set i stream(), s prost for cik\"l i if-elseâ€¦i to vze, che mina vsichki teastove (sled kato prochetoh pak, che se iska po-malkoto ascii, a ne po-goljamoto). Inache kazano KISS i si chetete vnimatelno uslovijata");
        firstComm.setDate(LocalDateTime.now());

        Comment secondComm = new Comment();
        secondComm.setId(2);
        secondComm.setAuthor(createUserAdmin());
        secondComm.setPostID(1);
        secondComm.setContent("Emili, imah absoljutno s\"shhata drama(na 1va)- nakraja prosto zamenih proverkata za naj- goljama stojnost, kojato bjah napravila s preobrazuvane na mapa v set i stream(), s prost for cik\"l i if-elseâ€¦i to vze, che mina vsichki teastove (sled kato prochetoh pak, che se iska po-malkoto ascii, a ne po-goljamoto). Inache kazano KISS i si chetete vnimatelno uslovijata");
        secondComm.setDate(LocalDateTime.now());

        commentList.add(firstComm);
        commentList.add(secondComm);
        post.setComments(commentList);

        User userAdmin = createUserAdmin();
        User userNormal = createUserNormal();
        User userBlocked = createBlockedUser();

        HashSet<User> likes = new HashSet<>();
        likes.add(userAdmin);
        likes.add(userNormal);
        likes.add(userBlocked);

        post.setLikes(likes);

        return post;
    }

    public static Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor(createUserNormal());
        comment.setPostID(1);
        comment.setContent("Emili, imah absoljutno s\"shhata drama(na 1va)- nakraja prosto zamenih proverkata za naj- goljama stojnost, kojato bjah napravila s preobrazuvane na mapa v set i stream(), s prost for cik\"l i if-elseâ€¦i to vze, che mina vsichki teastove (sled kato prochetoh pak, che se iska po-malkoto ascii, a ne po-goljamoto). Inache kazano KISS i si chetete vnimatelno uslovijata");
        comment.setDate(LocalDateTime.now());

        return comment;
    }

    public static CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setContent("Emili, imah absoljutno s\"shhata drama(na 1va)- nakraja prosto zamenih proverkata za naj- goljama stojnost, kojato bjah napravila s preobrazuvane na mapa v set i stream(), s prost for cik\"l i if-elseâ€¦i to vze, che mina vsichki teastove (sled kato prochetoh pak, che se iska po-malkoto ascii, a ne po-goljamoto). Inache kazano KISS i si chetete vnimatelno uslovijata");

        return comment;
    }

    public static User createUserAdmin() {
        User user = new User();
        user.setId(1);
        user.setUsername("tihomir.dimitrov.95");
        user.setFirstName("Tihomir");
        user.setLastName("Dimitrov");
        user.setPassword("Alabalabanan1!");
        user.setEmail("dimitrov@gmail.com");
        user.setRegistrationTime(LocalDateTime.now());
        user.setAdmin(true);
        user.setBlocked(false);
        user.setDeleted(false);

        return user;
    }

    public static User createUserNormal() {
        User user = new User();
        user.setId(2);
        user.setUsername("tihomir.dimitrov.95");
        user.setFirstName("Velin");
        user.setLastName("Dimitrov");
        user.setPassword("Alabalabanan1!");
        user.setEmail("dimitrov@gmail.com");
        user.setRegistrationTime(LocalDateTime.now());
        user.setAdmin(false);
        user.setBlocked(false);
        user.setDeleted(false);

        return user;
    }

    public static User createBlockedUser() {
        User user = new User();
        user.setId(3);
        user.setUsername("tihomir.dimitrov.95");
        user.setFirstName("Ivan");
        user.setLastName("Dimitrov");
        user.setPassword("Alabalabanan1!");
        user.setEmail("dimitrov@gmail.com");
        user.setRegistrationTime(LocalDateTime.now());
        user.setAdmin(false);
        user.setBlocked(true);
        user.setDeleted(false);

        return user;
    }

    public static User createDeletedUser() {
        User user = new User();
        user.setId(4);
        user.setUsername("tihomir.dimitrov.95");
        user.setFirstName("Dimitar");
        user.setLastName("Dimitrov");
        user.setPassword("Alabalabanan1!");
        user.setEmail("dimitrov@gmail.com");
        user.setRegistrationTime(LocalDateTime.now());
        user.setAdmin(false);
        user.setBlocked(false);
        user.setDeleted(true);

        return user;
    }

    public static UserDTOInImpl getUserDtoIn(User user) {
        return new UserDTOInImpl(user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getEmail(),
                null);
    }

    public static UserDTOOut getUserToDTOOut(User user) {
        return new UserDTOOut(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getRegistrationTime(),
                user.getPhoto(), user.isAdmin(), user.isBlocked(), user.isDeleted(),
                user.getPhoneNumber(), user.isEnabled());
    }

    public static UserDTOOutOwner getUserToDTOOutOwner(User user) {
        return new UserDTOOutOwner(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getPassword(), user.getEmail(), user.getRegistrationTime(),
                user.getPhoto(), user.isAdmin(), user.isBlocked(), user.isDeleted(),
                user.getPhoneNumber(), user.isEnabled());
    }

    /**
     * Accepts an object and returns the stringified object.
     * Useful when you need to pass a body to a HTTP request.
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
