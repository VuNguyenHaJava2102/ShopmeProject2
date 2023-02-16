package com.shopme.admin.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileUploadUtils {

    public static void saveFile(String uploadDir,
                                String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadDirPath = Paths.get(uploadDir);

        if(!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        try {
            InputStream inputStream = multipartFile.getInputStream();
            Path filePath = uploadDirPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Save file: " + fileName + " successfully");
        } catch(IOException ex) {
            log.error("Could not save file: " + fileName);
        }
    }

    public static void cleanDir(String uploadDir) {
        Path uploadDirPath = Paths.get(uploadDir);

        try {
            Files.list(uploadDirPath).forEach(file -> {
                if(!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                        log.info("Clean directory: " + uploadDir + " successfully!");
                    } catch(IOException ex) {
                        log.error("Could not delete file: " + file);
                    }
                }
            });
        } catch(IOException ex) {
            log.error("Could not list directory: " + uploadDirPath);
        }
    }

    public static void deleteUploadDir(String uploadDir) {
        // When we delete a directory, we need delete all the files in this directory first
        cleanDir(uploadDir);

        // Delete directory
        Path uploadDirPath = Paths.get(uploadDir);
        try {
            Files.delete(uploadDirPath);
        } catch(IOException ex) {
            log.error("Could not delete directory: " + uploadDirPath);
        }
    }

}
