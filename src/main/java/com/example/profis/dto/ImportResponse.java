package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private String status; // SUCCESS, PARTIAL, FAILED
    private Integer importedRecords;
    private Integer skippedRecords;
    private List<ImportError> errors;
    
    public ImportResponse(String status) {
        this.status = status;
        this.importedRecords = 0;
        this.skippedRecords = 0;
        this.errors = new ArrayList<>();
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private Integer row;
        private String field;
        private String message;
    }
}
