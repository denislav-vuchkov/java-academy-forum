package com.forum.javaforum.controllers.mvc;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.services.contracts.CommentService;
import com.forum.javaforum.services.contracts.PostService;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public HomeMvcController(PostService postService, CommentService commentService,
                             UserService userService, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showHomePage() {
        return "index";
    }

    @ModelAttribute("mostRecentPosts")
    public List<Post> getMostRecentPosts() {
        return postService.getMostRecent();
    }

    @ModelAttribute("mostCommentedPosts")
    public List<Post> getMostCommentedPosts() {
        return postService.getMostCommented();
    }

    @ModelAttribute("mostLikedPosts")
    public List<Post> getMostLikedPosts() {
        return postService.getMostLiked();
    }

    @ModelAttribute("registeredUsersCount")
    public Long getNumberOfUsers() {
        return userService.getNumberOfUsers();
    }

    @ModelAttribute("adminsCount")
    public Long getNumberOfAdmins() {
        return userService.getNumberOfAdmins();
    }

    @ModelAttribute("inactiveUsersCount")
    public Long getActiveUsersCount() {
        return userService.getNumberOfInactiveUsers();
    }

    @ModelAttribute("postsCount")
    public Long getPostsCount() {
        return postService.getNumberOfPosts();
    }

    @ModelAttribute("commentCount")
    public Long getCommentCount() {
        return commentService.getNumberOfComments();
    }

//    @ModelAttribute("averagePublicationsPerPerson")
//    public double averagePublicationsCount() {
//        return ((double) postService.getNumberOfPosts() / userService.getNumberOfUsers());
//    }

}
