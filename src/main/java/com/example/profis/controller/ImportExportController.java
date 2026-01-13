package com.example.profis.controller;

import com.example.profis.dto.ImportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImportExportController {

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponse> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String sheetName) {
        try {

            
            ImportResponse response = new ImportResponse("SUCCESS");
            response.setImportedRecords(0);
            response.setSkippedRecords(0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ImportResponse response = new ImportResponse("FAILED");
            ImportResponse.ImportError error = new ImportResponse.ImportError();
            error.setRow(0);
            error.setField("file");
            error.setMessage("Failed to process file: " + e.getMessage());
            response.getErrors().add(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam String type,
            @RequestParam(required = false) Long semesterId) {
        try {

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
