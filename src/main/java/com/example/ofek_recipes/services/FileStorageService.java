package com.example.ofek_recipes.services;

import com.example.ofek_recipes.configurations.UploadDirConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(UploadDirConfig uploadDirConfig) throws IOException {
        this.root = Paths.get(uploadDirConfig.getDir()).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public String save(MultipartFile file) throws IOException {
        String name = UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
         Path dest = root.resolve(name);
         Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
         return name;
    }
}
