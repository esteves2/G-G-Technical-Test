package com.CD.fileprocessor;

import com.CD.fileprocessor.exception.FileValidationException;
import com.CD.fileprocessor.model.OutcomeData;
import com.CD.fileprocessor.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingServiceTest {

    private FileProcessingService service;

    @BeforeEach
    void setUp() {
        service = new FileProcessingService();
    }

    // ========== VALID DATA TESTS ==========

    @Test
    @DisplayName("Should process valid file with single line and validation enabled")
    void testProcessFile_ValidSingleLine_WithValidation() throws FileValidationException {
        String validFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        List<OutcomeData> results = service.processFile(validFile, true);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getName());
        assertEquals("Rides A Bike", results.get(0).getTransport());
        assertEquals(12.1, results.get(0).getTopSpeed());
    }

    @Test
    @DisplayName("Should process valid file with multiple lines and validation enabled")
    void testProcessFile_ValidMultipleLines_WithValidation() throws FileValidationException {
        String validFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";

        List<OutcomeData> results = service.processFile(validFile, true);

        assertEquals(3, results.size());
        assertEquals("John Smith", results.get(0).getName());
        assertEquals("Mike Smith", results.get(1).getName());
        assertEquals("Jenny Walters", results.get(2).getName());
    }

    @Test
    @DisplayName("Should process valid file with validation disabled")
    void testProcessFile_ValidData_WithoutValidation() throws FileValidationException {
        String validFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        List<OutcomeData> results = service.processFile(validFile, false);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getName());
    }

    @Test
    @DisplayName("Should skip empty lines and process valid data")
    void testProcessFile_WithEmptyLines() throws FileValidationException {
        String fileWithEmptyLines = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3""";

        List<OutcomeData> results = service.processFile(fileWithEmptyLines, true);

        assertEquals(3, results.size());
    }

    @Test
    @DisplayName("Should handle fields with extra whitespace")
    void testProcessFile_WithExtraWhitespace() throws FileValidationException {
        String fileWithWhitespace = "18148426-89e1-11ee-b9d1-0242ac120002| 1X1D14 | John Smith | Likes Apricots | Rides A Bike | 6.2 | 12.1 ";

        List<OutcomeData> results = service.processFile(fileWithWhitespace, true);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getName());
        assertEquals("Rides A Bike", results.get(0).getTransport());
    }

    // ========== EMPTY/NULL FILE TESTS ==========

    @Test
    @DisplayName("Should throw exception for null file content")
    void testProcessFile_NullContent_ThrowsException() {
        assertThrows(FileValidationException.class, () -> service.processFile(null, true));
    }

    @Test
    @DisplayName("Should throw exception for empty file content")
    void testProcessFile_EmptyContent_ThrowsException() {
        assertThrows(FileValidationException.class, () -> service.processFile("", true));
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only file content")
    void testProcessFile_WhitespaceOnlyContent_ThrowsException() {
        assertThrows(FileValidationException.class, () -> service.processFile("   \n  \n   ", true));
    }

    // ========== FIELD COUNT VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for too few fields with validation enabled")
    void testProcessFile_TooFewFields_WithValidation() {
        String invalidFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidFile, true));
        assertTrue(exception.getMessage().contains("expected 7 fields"));
    }

    @Test
    @DisplayName("Should throw exception for too many fields with validation enabled")
    void testProcessFile_TooManyFields_WithValidation() {
        String invalidFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1|extra|field";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidFile, true));
        assertTrue(exception.getMessage().contains("expected 7 fields"));
    }

    @Test
    @DisplayName("Should skip lines with wrong field count when validation disabled")
    void testProcessFile_WrongFieldCount_WithoutValidation() throws FileValidationException {
        String mixedFile = """
                invalid|data|here
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                too|many|fields|here|and|more|and|even|more""";

        List<OutcomeData> results = service.processFile(mixedFile, false);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getName());
    }

    // ========== UUID VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for invalid UUID format with validation enabled")
    void testProcessFile_InvalidUUID_WithValidation() {
        String invalidUUID = "not-a-valid-uuid|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidUUID, true));
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }

    @Test
    @DisplayName("Should throw exception for empty UUID with validation enabled")
    void testProcessFile_EmptyUUID_WithValidation() {
        String emptyUUID = "|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(emptyUUID, true));
        assertTrue(exception.getMessage().contains("Invalid UUID format"));
    }

    @Test
    @DisplayName("Should skip line with invalid UUID when validation disabled")
    void testProcessFile_InvalidUUID_WithoutValidation() throws FileValidationException {
        String mixedFile = "not-a-uuid|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1\n" +
                "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5";

        List<OutcomeData> results = service.processFile(mixedFile, false);

        assertEquals(2, results.size());
        assertEquals("John Smith", results.get(0).getName());
    }

    // ========== EMPTY FIELD VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for empty ID field with validation enabled")
    void testProcessFile_EmptyID_WithValidation() {
        String emptyID = "18148426-89e1-11ee-b9d1-0242ac120002||John Smith|Likes Apricots|Rides A Bike|6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(emptyID, true));
        assertTrue(exception.getMessage().contains("ID field cannot be empty"));
    }

    @Test
    @DisplayName("Should throw exception for empty name field with validation enabled")
    void testProcessFile_EmptyName_WithValidation() {
        String emptyName = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14||Likes Apricots|Rides A Bike|6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(emptyName, true));
        assertTrue(exception.getMessage().contains("Name field cannot be empty"));
    }

    @Test
    @DisplayName("Should throw exception for empty transport field with validation enabled")
    void testProcessFile_EmptyTransport_WithValidation() {
        String emptyTransport = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots||6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(emptyTransport, true));
        assertTrue(exception.getMessage().contains("Transport field cannot be empty"));
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only name field with validation enabled")
    void testProcessFile_WhitespaceOnlyName_WithValidation() {
        String whitespaceField = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|   |Likes Apricots|Rides A Bike|6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(whitespaceField, true));
        assertTrue(exception.getMessage().contains("Name field cannot be empty"));
    }

    // ========== NUMBER FORMAT VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for invalid average speed format with validation enabled")
    void testProcessFile_InvalidAvgSpeedFormat_WithValidation() {
        String invalidNumber = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|not-a-number|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidNumber, true));
        assertTrue(exception.getMessage().contains("Invalid number format"));
    }

    @Test
    @DisplayName("Should throw exception for invalid top speed format with validation enabled")
    void testProcessFile_InvalidTopSpeedFormat_WithValidation() {
        String invalidNumber = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|abc";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidNumber, true));
        assertTrue(exception.getMessage().contains("Invalid number format"));
    }

    @Test
    @DisplayName("Should skip line with invalid number format when validation disabled")
    void testProcessFile_InvalidNumberFormat_WithoutValidation() throws FileValidationException {
        String mixedFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|not-a-number|12.1\n" +
                "3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5";

        List<OutcomeData> results = service.processFile(mixedFile, false);

        assertEquals(1, results.size());
        assertEquals("Mike Smith", results.get(0).getName());
    }

    // ========== SPEED VALIDATION TESTS ==========

    @Test
    @DisplayName("Should throw exception for negative average speed with validation enabled")
    void testProcessFile_NegativeAvgSpeed_WithValidation() {
        String negativeSpeed = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|-6.2|12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(negativeSpeed, true));
        assertTrue(exception.getMessage().contains("Average speed cannot be negative"));
    }

    @Test
    @DisplayName("Should throw exception for negative top speed with validation enabled")
    void testProcessFile_NegativeTopSpeed_WithValidation() {
        String negativeSpeed = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|-12.1";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(negativeSpeed, true));
        assertTrue(exception.getMessage().contains("Top speed cannot be negative"));
    }

    @Test
    @DisplayName("Should throw exception when top speed less than average speed with validation enabled")
    void testProcessFile_TopSpeedLessThanAvgSpeed_WithValidation() {
        String invalidSpeeds = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|50.0|25.0";

        FileValidationException exception = assertThrows(FileValidationException.class, () -> service.processFile(invalidSpeeds, true));
        assertTrue(exception.getMessage().contains("Top speed cannot be less than average speed"));
    }

    @Test
    @DisplayName("Should process negative speeds when validation disabled")
    void testProcessFile_NegativeSpeeds_WithoutValidation() throws FileValidationException {
        String negativeSpeed = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|-6.2|12.1";

        List<OutcomeData> results = service.processFile(negativeSpeed, false);

        assertEquals(1, results.size());
        assertEquals(12.1, results.get(0).getTopSpeed());
    }

    @Test
    @DisplayName("Should accept zero speeds with validation enabled")
    void testProcessFile_ZeroSpeeds_WithValidation() throws FileValidationException {
        String zeroSpeeds = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|0.0|0.0";

        List<OutcomeData> results = service.processFile(zeroSpeeds, true);

        assertEquals(1, results.size());
        assertEquals(0.0, results.get(0).getTopSpeed());
    }

    @Test
    @DisplayName("Should accept equal average and top speeds with validation enabled")
    void testProcessFile_EqualSpeeds_WithValidation() throws FileValidationException {
        String equalSpeeds = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|10.0|10.0";

        List<OutcomeData> results = service.processFile(equalSpeeds, true);

        assertEquals(1, results.size());
        assertEquals(10.0, results.get(0).getTopSpeed());
    }

    @Test
    @DisplayName("Should handle very large speed values")
    void testProcessFile_VeryLargeSpeeds() throws FileValidationException {
        String largeSpeeds = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|999999.99|9999999.99";

        List<OutcomeData> results = service.processFile(largeSpeeds, true);

        assertEquals(1, results.size());
        assertEquals(9999999.99, results.get(0).getTopSpeed());
    }

    @Test
    @DisplayName("Should handle decimal speeds with many decimal places")
    void testProcessFile_ManyDecimalPlaces() throws FileValidationException {
        String decimalSpeeds = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.123456|12.987654";

        List<OutcomeData> results = service.processFile(decimalSpeeds, true);

        assertEquals(1, results.size());
        assertEquals(12.987654, results.get(0).getTopSpeed());
    }

    // ========== MIXED VALID/INVALID DATA TESTS ==========

    @Test
    @DisplayName("Should throw exception on first invalid line with validation enabled")
    void testProcessFile_MixedData_WithValidation() {
        String mixedFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                invalid|data|here
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5""";

        assertThrows(FileValidationException.class, () -> service.processFile(mixedFile, true));
    }

    @Test
    @DisplayName("Should process only valid lines when validation disabled")
    void testProcessFile_MixedData_WithoutValidation() throws FileValidationException {
        String mixedFile = """
                invalid|data
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                not-a-uuid|1X1D14|Bad Line|Likes Stuff|Transport|1.0|2.0
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                too|few|fields""";

        List<OutcomeData> results = service.processFile(mixedFile, false);

        assertEquals(3, results.size());
        assertEquals("John Smith", results.get(0).getName());
        assertEquals("Bad Line", results.get(1).getName());
    }

    @Test
    @DisplayName("Should return empty list when all lines invalid with validation disabled")
    void testProcessFile_AllInvalidData_WithoutValidation() throws FileValidationException {
        String allInvalid = """
                invalid|data
                bad|line|here
                another|bad|one""";

        List<OutcomeData> results = service.processFile(allInvalid, false);

        assertTrue(results.isEmpty());
    }

    // ========== SPECIAL CHARACTERS AND EDGE CASES ==========

    @Test
    @DisplayName("Should handle names with special characters")
    void testProcessFile_SpecialCharactersInName() throws FileValidationException {
        String specialChars = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|O'Brien-Smith Jr.|Likes Apricots|Rides A Bike|6.2|12.1";

        List<OutcomeData> results = service.processFile(specialChars, true);

        assertEquals(1, results.size());
        assertEquals("O'Brien-Smith Jr.", results.get(0).getName());
    }

    @Test
    @DisplayName("Should handle transport with special characters")
    void testProcessFile_SpecialCharactersInTransport() throws FileValidationException {
        String specialChars = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A 2-Wheeler Bike™|6.2|12.1";

        List<OutcomeData> results = service.processFile(specialChars, true);

        assertEquals(1, results.size());
        assertEquals("Rides A 2-Wheeler Bike™", results.get(0).getTransport());
    }

    @Test
    @DisplayName("Should handle very long field values")
    void testProcessFile_VeryLongFieldValues() throws FileValidationException {
        String longName = "A".repeat(1000);
        String longTransport = "B".repeat(1000);
        String longFile = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|" + longName + "|Likes Apricots|" + longTransport + "|6.2|12.1";

        List<OutcomeData> results = service.processFile(longFile, true);

        assertEquals(1, results.size());
        assertEquals(longName, results.get(0).getName());
        assertEquals(longTransport, results.get(0).getTransport());
    }

    @Test
    @DisplayName("Should handle file with only one valid line among many invalid with validation disabled")
    void testProcessFile_OneValidAmongManyInvalid_WithoutValidation() throws FileValidationException {
        String file = """
                bad
                invalid|data
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                more|bad|data
                even|more|bad""";

        List<OutcomeData> results = service.processFile(file, false);

        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getName());
    }
}