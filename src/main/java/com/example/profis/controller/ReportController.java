package com.example.profis.controller;

import com.example.profis.dto.SwsCalculationReport;
import com.example.profis.dto.ThesesByProfessorReport;
import com.example.profis.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/theses-by-professor")
    public ResponseEntity<ThesesByProfessorReport> getThesesByProfessor(
            @RequestParam Long professorId,
            @RequestParam(required = false) Long semesterId) {
        try {
            ThesesByProfessorReport report = reportService.getThesesByProfessor(professorId, semesterId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sws-calculation")
    public ResponseEntity<SwsCalculationReport> calculateSws(
            @RequestParam Long professorId,
            @RequestParam(required = false) Long semesterId) {
        try {
            SwsCalculationReport report = reportService.calculateSws(professorId, semesterId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
