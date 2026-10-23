package org.isln.market.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private final String uploadPath;

    public FileService(@Value("${application.upload.path}") String uploadPath) {
        try {
            Path uploadDirectory = Paths.get(uploadPath);
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.uploadPath = uploadPath;
    }

    public Mono<String> save(FilePart file) {
        String filename = file.filename();
        return file.transferTo(Path.of(uploadPath, filename)).then(Mono.just(filename));
    }

    public Mono<Resource> getFile(String name) {
        return Mono.fromCallable(() -> new FileSystemResource(Path.of(uploadPath, name)));
    }
}
