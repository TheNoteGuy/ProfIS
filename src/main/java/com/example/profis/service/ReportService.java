package com.example.profis.service;

import com.example.profis.dto.SwsCalculationReport;
import com.example.profis.dto.ThesesByProfessorReport;
import com.example.profis.model.Referent;
import com.example.profis.model.Semester;
import com.example.profis.model.Student;
import com.example.profis.model.WissenschaftlicheArbeit;
import com.example.profis.repository.ReferentRepository;
import com.example.profis.repository.SemesterRepository;
import com.example.profis.repository.StudentRepository;
import com.example.profis.repository.WissenschaftlicheArbeitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final WissenschaftlicheArbeitRepository thesisRepository;
    private final ReferentRepository professorRepository;
    private final SemesterRepository semesterRepository;
    private final StudentRepository studentRepository;

    public ReportService(WissenschaftlicheArbeitRepository thesisRepository,
                        ReferentRepository professorRepository,
                        SemesterRepository semesterRepository,
                        StudentRepository studentRepository) {
        this.thesisRepository = thesisRepository;
        this.professorRepository = professorRepository;
        this.semesterRepository = semesterRepository;
        this.studentRepository = studentRepository;
    }

    public ThesesByProfessorReport getThesesByProfessor(Long professorId, Long semesterId) {
        try {
            Referent professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

            ThesesByProfessorReport report = new ThesesByProfessorReport();
            report.setProfessorId(professorId);
            report.setProfessorName(professor.getVorname() + " " + professor.getNachname());

            if (semesterId != null) {
                semesterRepository.findById(semesterId).ifPresent(semester -> {
                    report.setSemesterId(semesterId);
                    report.setSemesterName(semester.getName());
                });
            }

            // Get all theses for this professor
            List<WissenschaftlicheArbeit> allTheses = thesisRepository.findAll();
            List<WissenschaftlicheArbeit> professorTheses = allTheses.stream()
                .filter(t -> professorId.equals(t.getIdErstreferent()) || professorId.equals(t.getIdKorreferent()))
                .collect(Collectors.toList());

            // Calculate statistics
            ThesesByProfessorReport.Statistics stats = new ThesesByProfessorReport.Statistics();
            stats.setTotalTheses(professorTheses.size());
            stats.setBachelorTheses(0); // Would need type info from database
            stats.setMasterTheses(0);
            stats.setAsSupervisor((int) professorTheses.stream()
                .filter(t -> professorId.equals(t.getIdErstreferent())).count());
            stats.setAsCoSupervisor((int) professorTheses.stream()
                .filter(t -> professorId.equals(t.getIdKorreferent())).count());
            
            Map<String, Integer> byStatus = new HashMap<>();
            for (WissenschaftlicheArbeit thesis : professorTheses) {
                String status = determineStatus(thesis);
                byStatus.put(status, byStatus.getOrDefault(status, 0) + 1);
            }
            stats.setByStatus(byStatus);
            report.setStatistics(stats);

            // Create thesis summaries
            List<ThesesByProfessorReport.ThesisSummary> summaries = new ArrayList<>();
            for (WissenschaftlicheArbeit thesis : professorTheses) {
                ThesesByProfessorReport.ThesisSummary summary = new ThesesByProfessorReport.ThesisSummary();
                summary.setId(thesis.getIdWissenschaftlicheArbeiten());
                summary.setTitle(thesis.getTitel());
                summary.setType("BACHELOR"); // Default
                summary.setStatus(determineStatus(thesis));
                
                if (thesis.getMatrikelnummer() != null) {
                    studentRepository.findById(thesis.getMatrikelnummer()).ifPresent(student ->
                        summary.setStudent(student.getVorname() + " " + student.getNachname())
                    );
                }
                
                if (professorId.equals(thesis.getIdErstreferent())) {
                    summary.setRole("SUPERVISOR");
                } else {
                    summary.setRole("CO_SUPERVISOR");
                }
                
                summaries.add(summary);
            }
            report.setTheses(summaries);

            return report;
        } catch (SQLException e) {
            throw new RuntimeException("Error generating report", e);
        }
    }

    public SwsCalculationReport calculateSws(Long professorId, Long semesterId) {
        try {
            Referent professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

            SwsCalculationReport report = new SwsCalculationReport();
            report.setProfessorId(professorId);
            report.setProfessorName(professor.getVorname() + " " + professor.getNachname());

            if (semesterId != null) {
                semesterRepository.findById(semesterId).ifPresent(semester -> {
                    report.setSemesterId(semesterId);
                    report.setSemesterName(semester.getName());
                });
            }

            // Get all theses for this professor
            List<WissenschaftlicheArbeit> allTheses = thesisRepository.findAll();
            List<WissenschaftlicheArbeit> professorTheses = allTheses.stream()
                .filter(t -> professorId.equals(t.getIdErstreferent()) || professorId.equals(t.getIdKorreferent()))
                .collect(Collectors.toList());

            // Default SWS rates (would typically come from program configuration)
            BigDecimal swsRateSupervisor = new BigDecimal("0.2");
            BigDecimal swsRateCoSupervisor = new BigDecimal("0.1");

            BigDecimal totalSwsAsSupervisor = BigDecimal.ZERO;
            BigDecimal totalSwsAsCoSupervisor = BigDecimal.ZERO;
            List<SwsCalculationReport.ThesisBreakdown> breakdown = new ArrayList<>();

            for (WissenschaftlicheArbeit thesis : professorTheses) {
                SwsCalculationReport.ThesisBreakdown item = new SwsCalculationReport.ThesisBreakdown();
                item.setThesisId(thesis.getIdWissenschaftlicheArbeiten());
                item.setThesisTitle(thesis.getTitel());
                item.setProgram("Unknown"); // Would need to fetch from database
                
                if (professorId.equals(thesis.getIdErstreferent())) {
                    item.setRole("SUPERVISOR");
                    item.setSws(swsRateSupervisor);
                    totalSwsAsSupervisor = totalSwsAsSupervisor.add(swsRateSupervisor);
                } else {
                    item.setRole("CO_SUPERVISOR");
                    item.setSws(swsRateCoSupervisor);
                    totalSwsAsCoSupervisor = totalSwsAsCoSupervisor.add(swsRateCoSupervisor);
                }
                
                breakdown.add(item);
            }

            BigDecimal totalSws = totalSwsAsSupervisor.add(totalSwsAsCoSupervisor);
            BigDecimal maxSwsAllowed = new BigDecimal("4.0"); // Default max
            BigDecimal swsRemaining = maxSwsAllowed.subtract(totalSws);

            SwsCalculationReport.SwsCalculation calc = new SwsCalculationReport.SwsCalculation();
            calc.setTotalSws(totalSws);
            calc.setSwsAsSupervisor(totalSwsAsSupervisor);
            calc.setSwsAsCoSupervisor(totalSwsAsCoSupervisor);
            calc.setMaxSwsAllowed(maxSwsAllowed);
            calc.setSwsRemaining(swsRemaining);
            calc.setIsOverLimit(totalSws.compareTo(maxSwsAllowed) > 0);

            report.setSwsCalculation(calc);
            report.setBreakdown(breakdown);

            return report;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating SWS", e);
        }
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
