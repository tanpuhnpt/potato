package com.ktpm.potatoapi.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String upload(MultipartFile file, String folderName);
}
