package com.example.profis.controller;

import com.example.profis.dto.ThesisRequest;
import com.example.profis.dto.ThesisResponse;
import com.example.profis.service.ThesisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theses")
@CrossOrigin(origins = "*")
public class ThesisController {

    private final ThesisService thesisService;

    public ThesisController(ThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @GetMapping
    public ResponseEntity<List<ThesisResponse>> getAllTheses(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long supervisorId,
            @RequestParam(required = false) Long studentId) {
        try {
            List<ThesisResponse> theses = thesisService.getAllTheses();
            // TODO: Add filtering logic based on parameters
            return ResponseEntity.ok(theses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-semester/{semesterId}")
    public ResponseEntity<List<ThesisResponse>> getThesesBySemester(@PathVariable Long semesterId) {
        try {
            List<ThesisResponse> theses = thesisService.getThesesBySemester(semesterId);
            return ResponseEntity.ok(theses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThesisResponse> getThesisById(@PathVariable Long id) {
        try {
            ThesisResponse response = thesisService.getThesisById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ThesisResponse> createThesis(@RequestBody ThesisRequest request) {
        try {
            ThesisResponse response = thesisService.createThesis(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThesisResponse> updateThesis(
            @PathVariable Long id,
            @RequestBody ThesisRequest request) {
        try {
            ThesisResponse response = thesisService.updateThesis(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThesis(@PathVariable Long id) {
        try {
            thesisService.deleteThesis(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/grades")
    public ResponseEntity<ThesisResponse> updateGrades(
            @PathVariable Long id,
            @RequestBody GradeUpdateRequest request) {
        try {
            ThesisResponse response = thesisService.updateGrades(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // DTO class for grade updates
    public static class GradeUpdateRequest {
        private Double noteArbeitReferent;
        private Double noteArbeitKorreferent;
        private Double noteKolloquiumReferent;
        private Double noteKolloquiumKorreferent;

        public Double getNoteArbeitReferent() {
            return noteArbeitReferent;
        }

        public void setNoteArbeitReferent(Double noteArbeitReferent) {
            this.noteArbeitReferent = noteArbeitReferent;
        }

        public Double getNoteArbeitKorreferent() {
            return noteArbeitKorreferent;
        }

        public void setNoteArbeitKorreferent(Double noteArbeitKorreferent) {
            this.noteArbeitKorreferent = noteArbeitKorreferent;
        }

        public Double getNoteKolloquiumReferent() {
            return noteKolloquiumReferent;
        }

        public void setNoteKolloquiumReferent(Double noteKolloquiumReferent) {
            this.noteKolloquiumReferent = noteKolloquiumReferent;
        }

        public Double getNoteKolloquiumKorreferent() {
            return noteKolloquiumKorreferent;
        }

        public void setNoteKolloquiumKorreferent(Double noteKolloquiumKorreferent) {
            this.noteKolloquiumKorreferent = noteKolloquiumKorreferent;
        }
    }
}