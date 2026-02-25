package in.ankitdaksh.billingsoftware.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    //uploading the file
    String uploadFile(MultipartFile file);
    boolean deleteFile(String imgUrl);
}
