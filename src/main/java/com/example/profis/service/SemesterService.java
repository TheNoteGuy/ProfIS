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
            
            // Determine if this should be the current semester
            LocalDate now = LocalDate.now();
            boolean isCurrent = !now.isBefore(request.getStartDate()) && !now.isAfter(request.getEndDate());
            semester.setIsCurrent(isCurrent);
            
            Semester saved = semesterRepository.save(semester);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating semester", e);
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
