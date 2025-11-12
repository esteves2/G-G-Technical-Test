package com.CD.fileprocessor.service;

import com.CD.fileprocessor.exception.FileValidationException;
import com.CD.fileprocessor.model.OutcomeData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileProcessingService {

    private static final int UUID_INDEX = 0;
    private static final int ID_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int LIKES_INDEX = 3;
    private static final int TRANSPORT_INDEX = 4;
    private static final int AVG_SPEED_INDEX = 5;
    private static final int TOP_SPEED_INDEX = 6;
    private static final int EXPECTED_FIELD_COUNT = 7;

    public List<OutcomeData> processFile(String fileContents, boolean validate) throws FileValidationException {

        if(fileContents == null || fileContents.trim().isEmpty()) {
            throw new FileValidationException("File content is empty");
        }

        List<OutcomeData> outcomeDataList = new ArrayList<>();
        String[] lines = fileContents.split("\n");

        for(String line : lines) {
            if(line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split("\\|");

            if(validate) {
                validateFields(fields);
            }

            try {
                OutcomeData outcome = createOutcomeData(fields, validate);
                outcomeDataList.add(outcome);

            } catch(NumberFormatException e) {
                if(validate) {
                    throw new FileValidationException("Invalid number format in line: " + line);
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                if(validate) {
                    throw new FileValidationException("Invalid data format in line: " + line);
                }
            }
        }

        return outcomeDataList;
    }

    private void validateFields(String[] fields) throws FileValidationException {
        if(fields.length != EXPECTED_FIELD_COUNT) {
            throw new FileValidationException("Invalid line format: expected " + EXPECTED_FIELD_COUNT + " fields, got " + fields.length);
        }

        if(!isValidUUID(fields[UUID_INDEX].trim())) {
            throw new FileValidationException("Invalid UUID format: " + fields[UUID_INDEX]);
        }

        if(fields[ID_INDEX].trim().isEmpty()) {
            throw new FileValidationException("ID field cannot be empty");
        }

        if(fields[NAME_INDEX].trim().isEmpty()) {
            throw new FileValidationException("Name field cannot be empty");
        }

        if(fields[TRANSPORT_INDEX].trim().isEmpty()) {
            throw new FileValidationException("Transport field cannot be empty");
        }
    }

    private OutcomeData createOutcomeData(String[] fields, boolean validate) throws FileValidationException {
        double avgSpeed = Double.parseDouble(fields[AVG_SPEED_INDEX].trim());
        double topSpeed = Double.parseDouble(fields[TOP_SPEED_INDEX].trim());

        if(validate) {
            validateSpeeds(avgSpeed, topSpeed);
        }

        return new OutcomeData(
                fields[NAME_INDEX].trim(),
                fields[TRANSPORT_INDEX].trim(),
                topSpeed
        );
    }

    private void validateSpeeds(double avgSpeed, double topSpeed) throws FileValidationException {
        if(avgSpeed < 0) {
            throw new FileValidationException("Average speed cannot be negative: " + avgSpeed);
        }
        if(topSpeed < 0) {
            throw new FileValidationException("Top speed cannot be negative: " + topSpeed);
        }
        if(topSpeed < avgSpeed) {
            throw new FileValidationException("Top speed cannot be less than average speed");
        }
    }

    private boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}