package de.hsel.tetris.usermanagement.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService {

    @Value("${files.location}")
    private String location;

    public void store(MultipartFile file, String filename) throws IOException {
        Path path = Paths.get(location + "/" + filename);
        Files.write(path, file.getBytes());
    }

    public byte[] load(String filename) throws IOException {
        var path = Paths.get(location, filename);
        return Files.readAllBytes(path);
    }

}
