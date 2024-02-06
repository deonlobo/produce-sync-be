package com.boom.producesyncbe.sellerService.impl;

import com.boom.producesyncbe.sellerService.FileService;
import org.apache.coyote.BadRequestException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    DataBucketUtil dataBucketUtil;
    @Override
    public ResponseEntity<List<String>> uploadFiles(MultipartFile[] files, String directory) {
        List<String> inputFiles = new ArrayList<>();

        Arrays.asList(files).forEach(file -> {
            String originalFileName = file.getOriginalFilename();
            if(originalFileName == null){
                throw new RuntimeException("Original file name is null");
            }
            Path path = new File(originalFileName).toPath();

            try {
                String contentType = Files.probeContentType(path);
                String fileUrl = dataBucketUtil.uploadFile(file, originalFileName, contentType, directory);

                if (fileUrl != null) {
                    inputFiles.add(fileUrl);
                    System.out.println("File uploaded successfully, url: {}"+fileUrl );
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading");
            }
        });
        System.out.println("here");
        return ResponseEntity.ok(inputFiles);
    }
}
