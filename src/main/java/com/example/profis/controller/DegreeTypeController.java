package com.example.profis.controller;

import com.example.profis.dto.DegreeTypeRequest;
import com.example.profis.dto.DegreeTypeResponse;
import com.example.profis.service.DegreeTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/degree-types")
@CrossOrigin(origins = "*")
public class DegreeTypeController {

    private final DegreeTypeService degreeTypeService;

    public DegreeTypeController(DegreeTypeService degreeTypeService) {
        this.degreeTypeService = degreeTypeService;
    }

    @GetMapping
    public ResponseEntity<List<DegreeTypeResponse>> getAllDegreeTypes() {
        try {
            List<DegreeTypeResponse> degreeTypes = degreeTypeService.getAllDegreeTypes();
            return ResponseEntity.ok(degreeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DegreeTypeResponse> getDegreeTypeById(@PathVariable Long id) {
        try {
            DegreeTypeResponse response = degreeTypeService.getDegreeTypeById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<DegreeTypeResponse> createDegreeType(@RequestBody DegreeTypeRequest request) {
        try {
            DegreeTypeResponse response = degreeTypeService.createDegreeType(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DegreeTypeResponse> updateDegreeType(
            @PathVariable Long id,
            @RequestBody DegreeTypeRequest request) {
        try {
            DegreeTypeResponse response = degreeTypeService.updateDegreeType(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDegreeType(@PathVariable Long id) {
        try {
            degreeTypeService.deleteDegreeType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}