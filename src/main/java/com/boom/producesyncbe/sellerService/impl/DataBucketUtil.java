package com.boom.producesyncbe.sellerService.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.io.FileUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataBucketUtil {

    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private String gcpBucketId;


    public String uploadFile(MultipartFile multipartFile, String fileName, String contentType, String directory) {

        try{

            byte[] fileData = FileUtils.readFileToByteArray(convertFile(multipartFile));

            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();

            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();

            Storage storage = options.getService();
            Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());

            // Generate a random integer between 0 (inclusive) and 100 (exclusive)
            checkFileExtension(fileName);
            Random random = new Random();
            int randomNumber = random.nextInt(100);
            Blob blob = bucket.create( directory.replace(":","") + "/" + randomNumber + fileName , fileData, contentType);

            if(blob != null){
                return blob.getName();
            }

        }catch (Exception e){
            throw new RuntimeException("An error occurred while storing data to GCS");
        }
        throw new RuntimeException("An error occurred while storing data to GCS");
    }

    private File convertFile(MultipartFile file) {

        try{
            if(file.getOriginalFilename() == null){
                throw new RuntimeException("Original file name is null");
            }
            File convertedFile = new File(file.getOriginalFilename());
            FileOutputStream outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            return convertedFile;
        }catch (Exception e){
            throw new RuntimeException("An error has occurred while converting the file");
        }
    }

    private String checkFileExtension(String fileName) {
        if(fileName != null && fileName.contains(".")){
            String[] extensionList = {".png", ".jpeg", ".jpg"};

            for(String extension: extensionList) {
                if (fileName.endsWith(extension)) {
                    return extension;
                }
            }
        }
        throw new RuntimeException("Not a permitted file type");
    }
}
