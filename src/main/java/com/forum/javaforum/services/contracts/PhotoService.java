package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {

    User save(MultipartFile photoAsFile, int userID) throws IOException;

    User update(MultipartFile photoAsFile, int userID) throws IOException;

    User delete(int userID) throws IOException;

}
