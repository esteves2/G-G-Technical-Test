package com.CD.fileprocessor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileProcessorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Integration: Should successfully process valid file with validation enabled")
    void testProcessFile_ValidFile_WithValidation() throws Exception {
        String fileContent = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1\n" +
                "3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                "text/plain",
                fileContent.getBytes()
        );

        mockMvc.perform(multipart("/elemental-concept/process")
                        .file(file)
                        .param("validate", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[1].name").value("Mike Smith"));
    }

    @Test
    @DisplayName("Integration: Should return 400 with proper error format for invalid file")
    void testProcessFile_InvalidFile_WithValidation() throws Exception {
        String fileContent = "invalid|data|here";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "BadFile.txt",
                "text/plain",
                fileContent.getBytes()
        );

        mockMvc.perform(multipart("/elemental-concept/process")
                        .file(file)
                        .param("validate", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Integration: Should skip invalid lines when validation disabled")
    void testProcessFile_FeatureFlag_ValidationDisabled() throws Exception {
        String fileContent = "invalid|data\n" +
                "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "MixedFile.txt",
                "text/plain",
                fileContent.getBytes()
        );

        mockMvc.perform(multipart("/elemental-concept/process")
                        .file(file)
                        .param("validate", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Smith"));
    }

    @Test
    @DisplayName("Integration: Should use default validation=true when parameter not provided")
    void testProcessFile_DefaultValidation() throws Exception {
        String fileContent = "invalid|data";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "BadFile.txt",
                "text/plain",
                fileContent.getBytes()
        );

        mockMvc.perform(multipart("/elemental-concept/process")
                        .file(file))
                .andExpect(status().isBadRequest());
    }
}