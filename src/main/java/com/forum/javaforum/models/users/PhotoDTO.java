package com.forum.javaforum.models.users;

import org.springframework.web.multipart.MultipartFile;

public class PhotoDTO {

    private MultipartFile photo;

    public PhotoDTO() {
    }

    public PhotoDTO(MultipartFile photo) {
        this.photo = photo;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }
}
