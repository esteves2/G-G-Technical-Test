package com.CD.fileprocessor.controller;

import com.CD.fileprocessor.model.OutcomeData;
import com.CD.fileprocessor.service.FileProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/elemental-concept")
public class FileProcessorController {

    private final FileProcessingService fileProcessingService;

    public FileProcessorController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "validate", defaultValue = "true") boolean validate
    ) throws Exception {
        String fileContent = new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        List<OutcomeData> results = fileProcessingService.processFile(fileContent, validate);
        return ResponseEntity.ok(results);
    }
}
