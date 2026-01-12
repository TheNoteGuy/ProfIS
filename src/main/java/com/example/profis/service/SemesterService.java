package com.example.profis.service;

import com.example.profis.dto.SemesterRequest;
import com.example.profis.dto.SemesterResponse;
import com.example.profis.model.Semester;
import com.example.profis.repository.SemesterRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public List<SemesterResponse> getAllSemesters() {
        try {
            List<Semester> semesters = semesterRepository.findAll();
            List<SemesterResponse> responses = new ArrayList<>();

            for (Semester semester : semesters) {
                responses.add(mapToResponse(semester));
            }

            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching semesters", e);
        }
    }

    public SemesterResponse getSemesterById(Long id) {
        try {
            Semester semester = semesterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
            return mapToResponse(semester);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching semester", e);
        }
    }

    public SemesterResponse createSemester(SemesterRequest request) {
        try {
            Semester semester = new Semester();
            semester.setName(request.getName());
            semester.setType(request.getType());
            semester.setStartDate(request.getStartDate());
            semester.setEndDate(request.getEndDate());

            LocalDate now = LocalDate.now();
            boolean isCurrent = !now.isBefore(request.getStartDate()) && !now.isAfter(request.getEndDate());
            semester.setIsCurrent(isCurrent);

            Semester saved = semesterRepository.save(semester);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating semester", e);
        }
    }

    public SemesterResponse updateSemester(Long id, SemesterRequest request) {
        try {
            Semester existing = semesterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));

            existing.setName(request.getName());
            existing.setType(request.getType());
            existing.setStartDate(request.getStartDate());
            existing.setEndDate(request.getEndDate());

            LocalDate now = LocalDate.now();
            boolean isCurrent = !now.isBefore(request.getStartDate()) && !now.isAfter(request.getEndDate());
            existing.setIsCurrent(isCurrent);

            Semester updated = semesterRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating semester", e);
        }
    }

    public void deleteSemester(Long id) {
        try {
            semesterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
            semesterRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting semester", e);
        }
    }

    /**
     * Findet das passende Semester für ein gegebenes Datum
     */
    public Semester findSemesterByDate(LocalDate date) {
        try {
            return semesterRepository.findByDate(date)
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding semester by date", e);
        }
    }

    /**
     * Aktualisiert alle Semester-Status basierend auf dem aktuellen Datum
     */
    public void updateCurrentSemesterStatus() {
        try {
            semesterRepository.updateCurrentStatus();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating semester status", e);
        }
    }

    /**
     * Gibt das aktuell aktive Semester zurück
     */
    public SemesterResponse getCurrentSemester() {
        try {
            Semester semester = semesterRepository.findCurrentSemester()
                    .orElseThrow(() -> new RuntimeException("No current semester found"));
            return mapToResponse(semester);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching current semester", e);
        }
    }

    private SemesterResponse mapToResponse(Semester semester) {
        SemesterResponse response = new SemesterResponse();
        response.setId(semester.getIdSemester());
        response.setName(semester.getName());
        response.setType(semester.getType());
        response.setStartDate(semester.getStartDate());
        response.setEndDate(semester.getEndDate());
        response.setIsCurrent(semester.getIsCurrent());
        return response;
    }
}