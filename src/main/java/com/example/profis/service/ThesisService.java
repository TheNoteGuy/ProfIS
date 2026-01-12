package com.example.profis.service;

import com.example.profis.dto.ThesisRequest;
import com.example.profis.dto.ThesisResponse;
import com.example.profis.model.Referent;
import com.example.profis.model.Student;
import com.example.profis.model.WissenschaftlicheArbeit;
import com.example.profis.repository.ReferentRepository;
import com.example.profis.repository.StudentRepository;
import com.example.profis.repository.WissenschaftlicheArbeitRepository;
import org.springframework.stereotype.Service;
import com.example.profis.service.SwsCalculationService.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThesisService {

    private final WissenschaftlicheArbeitRepository thesisRepository;
    private final StudentRepository studentRepository;
    private final ReferentRepository professorRepository;
    private final SwsCalculationService swsCalculationService;

    public ThesisService(WissenschaftlicheArbeitRepository thesisRepository,
                        StudentRepository studentRepository,
                        ReferentRepository professorRepository,SwsCalculationService swsCalculationService) {
        this.thesisRepository = thesisRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.swsCalculationService = swsCalculationService;
    }

    public List<ThesisResponse> getAllTheses() {
        try {
            List<WissenschaftlicheArbeit> theses = thesisRepository.findAll();
            List<ThesisResponse> responses = new ArrayList<>();
            
            for (WissenschaftlicheArbeit thesis : theses) {
                responses.add(mapToResponse(thesis));
            }
            
            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching theses", e);
        }
    }

    public List<ThesisResponse> getThesesBySemester(Long semesterId) throws SQLException {
        List<WissenschaftlicheArbeit> allTheses = thesisRepository.findAll();
        List<ThesisResponse> result = new ArrayList<>();

        for (WissenschaftlicheArbeit thesis : allTheses) {
            if (swsCalculationService.thesisBelongsToSemester(thesis, semesterId)) {
                result.add(mapToResponse(thesis));
            }
        }

        return result;
    }

    public ThesisResponse getThesisById(Long id) {
        try {
            WissenschaftlicheArbeit thesis = thesisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thesis not found with id: " + id));
            return mapToResponse(thesis);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching thesis", e);
        }
    }

    public ThesisResponse createThesis(ThesisRequest request) {
        try {
            WissenschaftlicheArbeit thesis = new WissenschaftlicheArbeit();
            thesis.setMatrikelnummer(request.getMatrikelnummer());
            thesis.setIdErstreferent(request.getSupervisorId());
            thesis.setIdKorreferent(request.getCoSupervisorId());
            thesis.setTitel(request.getTitle());
            thesis.setStartDatum(request.getStartDate());
            thesis.setAbgabeDatum(request.getSubmissionDate());
            thesis.setEndeKorrektur(request.getCorrectionEndDate());
            thesis.setKolloquimBeginn(request.getColloquiumStart());
            thesis.setKolloquimEnde(request.getColloquiumEnd());
            thesis.setNoteArbeitReferent(request.getGradeSupervisor());
            thesis.setNoteArbeitKorreferent(request.getGradeCoSupervisor());
            thesis.setNoteKolloquiumReferent(request.getGradeColloquiumSupervisor());
            thesis.setNoteKolloquiumKorreferent(request.getGradeColloquiumCoSupervisor());
            
            WissenschaftlicheArbeit saved = thesisRepository.save(thesis);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating thesis", e);
        }
    }

    public ThesisResponse updateThesis(Long id, ThesisRequest request) {
        try {
            WissenschaftlicheArbeit existing = thesisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thesis not found with id: " + id));
            
            existing.setMatrikelnummer(request.getMatrikelnummer());
            existing.setIdErstreferent(request.getSupervisorId());
            existing.setIdKorreferent(request.getCoSupervisorId());
            existing.setTitel(request.getTitle());
            existing.setStartDatum(request.getStartDate());
            existing.setAbgabeDatum(request.getSubmissionDate());
            existing.setEndeKorrektur(request.getCorrectionEndDate());
            existing.setKolloquimBeginn(request.getColloquiumStart());
            existing.setKolloquimEnde(request.getColloquiumEnd());
            existing.setNoteArbeitReferent(request.getGradeSupervisor());
            existing.setNoteArbeitKorreferent(request.getGradeCoSupervisor());
            existing.setNoteKolloquiumReferent(request.getGradeColloquiumSupervisor());
            existing.setNoteKolloquiumKorreferent(request.getGradeColloquiumCoSupervisor());
            
            WissenschaftlicheArbeit updated = thesisRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating thesis", e);
        }
    }

    public void deleteThesis(Long id) {
        try {
            thesisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thesis not found with id: " + id));
            thesisRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting thesis", e);
        }
    }

    private ThesisResponse mapToResponse(WissenschaftlicheArbeit thesis) throws SQLException {
        ThesisResponse response = new ThesisResponse();
        response.setId(thesis.getIdWissenschaftlicheArbeiten());
        response.setTitle(thesis.getTitel());
        response.setStudentMatriculationNumber(thesis.getMatrikelnummer());
        
        // Fetch student name
        if (thesis.getMatrikelnummer() != null) {
            studentRepository.findById(thesis.getMatrikelnummer()).ifPresent(student ->
                response.setStudentName(student.getVorname() + " " + student.getNachname())
            );
        }
        
        // Fetch supervisor info
        response.setSupervisorId(thesis.getIdErstreferent());
        if (thesis.getIdErstreferent() != null) {
            professorRepository.findById(thesis.getIdErstreferent()).ifPresent(supervisor ->
                response.setSupervisorName(supervisor.getVorname() + " " + supervisor.getNachname())
            );
        }
        
        // Fetch co-supervisor info
        response.setCoSupervisorId(thesis.getIdKorreferent());
        if (thesis.getIdKorreferent() != null) {
            professorRepository.findById(thesis.getIdKorreferent()).ifPresent(coSupervisor ->
                response.setCoSupervisorName(coSupervisor.getVorname() + " " + coSupervisor.getNachname())
            );
        }
        
        response.setStartDate(thesis.getStartDatum());
        response.setSubmissionDate(thesis.getAbgabeDatum());
        response.setCorrectionEndDate(thesis.getEndeKorrektur());
        response.setColloquiumStart(thesis.getKolloquimBeginn());
        response.setColloquiumEnd(thesis.getKolloquimEnde());
        response.setGradeSupervisor(thesis.getNoteArbeitReferent());
        response.setGradeCoSupervisor(thesis.getNoteArbeitKorreferent());
        response.setGradeColloquiumSupervisor(thesis.getNoteKolloquiumReferent());
        response.setGradeColloquiumCoSupervisor(thesis.getNoteKolloquiumKorreferent());
        
        // Calculate final grade
        response.setFinalGrade(calculateFinalGrade(thesis));
        
        // Determine status and type (simplified logic)
        response.setStatus(determineStatus(thesis));
        response.setType("BACHELOR"); // Default, would need additional logic
        
        return response;
    }

    private BigDecimal calculateFinalGrade(WissenschaftlicheArbeit thesis) {
        List<BigDecimal> grades = new ArrayList<>();
        if (thesis.getNoteArbeitReferent() != null) grades.add(thesis.getNoteArbeitReferent());
        if (thesis.getNoteArbeitKorreferent() != null) grades.add(thesis.getNoteArbeitKorreferent());
        if (thesis.getNoteKolloquiumReferent() != null) grades.add(thesis.getNoteKolloquiumReferent());
        if (thesis.getNoteKolloquiumKorreferent() != null) grades.add(thesis.getNoteKolloquiumKorreferent());
        
        if (grades.isEmpty()) return null;
        
        BigDecimal sum = grades.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);
    }

    private String determineStatus(WissenschaftlicheArbeit thesis) {
        if (thesis.getNoteArbeitReferent() != null && thesis.getNoteArbeitKorreferent() != null &&
            thesis.getNoteKolloquiumReferent() != null && thesis.getNoteKolloquiumKorreferent() != null) {
            return "COMPLETED";
        } else if (thesis.getAbgabeDatum() != null) {
            return "SUBMITTED";
        } else if (thesis.getStartDatum() != null) {
            return "IN_PROGRESS";
        } else {
            return "PLANNED";
        }
    }
}
