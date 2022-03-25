package com.forum.javaforum.services;

import com.forum.javaforum.models.users.Photo;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.PhotoRepository;
import com.forum.javaforum.services.contracts.UserService;
import com.forum.javaforum.utilities.CloudinaryHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static com.forum.javaforum.HelperForTisho.createUserNormal;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceImplTests {

    @Mock
    PhotoRepository mockPhotoRepo;

    @Mock
    CloudinaryHelper cloudinaryHelper;

    @Mock
    UserService userService;

    @InjectMocks
    PhotoServiceImpl service;

    @Test
    void save_should_callRepoAndCloudinary_When_Called() throws IOException {
        MockMultipartFile file = new MockMultipartFile("random", new byte[2]);
        User user = createUserNormal();
        user.setPhoto(null);

        Mockito.when(userService.getById(2)).thenReturn(user);

        Mockito.when(cloudinaryHelper.uploadToCloudinary(file, user))
                        .thenReturn("random string");

        service.save(file, 2);

        Mockito.verify(cloudinaryHelper, Mockito.times(1)).uploadToCloudinary(file, user);
        Mockito.verify(mockPhotoRepo, Mockito.times(1)).save(new Photo(2, "random string"));
    }

    @Test
    void update_should_callRepoAndCloudinary_When_Called() throws IOException {
        MockMultipartFile file = new MockMultipartFile("random", new byte[2]);
        User user = createUserNormal();
        user.setPhoto(null);

        Mockito.when(userService.getById(2)).thenReturn(user);

        Mockito.when(cloudinaryHelper.uploadToCloudinary(file, user))
                .thenReturn("random string");

        service.update(file, 2);

        Mockito.verify(cloudinaryHelper, Mockito.times(1)).uploadToCloudinary(file, user);
        Mockito.verify(mockPhotoRepo, Mockito.times(1)).update(new Photo(2, "random string"));
    }

    @Test
    void delete_should_callRepo_When_Called() throws Exception {
        MockMultipartFile file = new MockMultipartFile("random", new byte[2]);
        User user = createUserNormal();
        user.setPhoto(null);

        Mockito.when(userService.getById(2)).thenReturn(user);

        service.delete(2);

        Mockito.verify(mockPhotoRepo, Mockito.times(1)).delete(user.getPhoto());
    }

}


