package com.boom.producesyncbe.sellerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    ResponseEntity<List<String>> uploadFiles(MultipartFile[] files, String directory);
}
