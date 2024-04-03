package com.veagud.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileDownloadController {

    private final ResourceLoader resourceLoader;

    private final Path rootLocation = Paths.get("E:/folderTren");
    private final Path rootLocationPdf = Paths.get("C:/Users/Loshadka/Documents");
    // Constructor
    public FileDownloadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path file = rootLocation.resolve(filename + ".dwg");
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/filespdf/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFilePdf(@PathVariable String filename) {
        try {
            Path file = rootLocationPdf.resolve(filename + ".pdf");
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
