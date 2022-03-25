package com.forum.javaforum.services;

import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PhotoRepository;
import com.forum.javaforum.services.contracts.PhotoService;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.CloudinaryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository repository;
    private final CloudinaryHelper cloudinaryHelper;
    private final UserService userService;

    @Autowired
    public PhotoServiceImpl(PhotoRepository repository, CloudinaryHelper cloudinaryHelper, UserService userService) {
        this.repository = repository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.userService = userService;
    }

    public User save(MultipartFile photoAsFile, int userID) throws IOException {
        User user = userService.getById(userID);

        String photoURL = cloudinaryHelper.uploadToCloudinary(photoAsFile, user);

        Photo photo = new Photo(user.getId(), photoURL);
        repository.save(photo);

        return userService.getById(userID);
    }

    public User update(MultipartFile photoAsFile, int userID) throws IOException {
        User user = userService.getById(userID);
        String photoURL = cloudinaryHelper.uploadToCloudinary(photoAsFile, user);

        Photo photo = new Photo(user.getId(), photoURL);
        repository.update(photo);

        return userService.getById(userID);
    }

    public User delete(int userID) throws IOException {
        User user = userService.getById(userID);

        cloudinaryHelper.deleteFromCloudinary(userID);

        repository.delete(user.getPhoto());

        return userService.getById(userID);
    }
}
