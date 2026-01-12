package com.example.profis.service;

import com.example.profis.dto.SwsCalculationReport;
import com.example.profis.dto.SwsCalculationReport.SwsCalculation;
import com.example.profis.dto.SwsCalculationReport.SwsCalculationConfig;
import com.example.profis.dto.SwsCalculationReport.ThesisBreakdown;
import com.example.profis.model.*;
import com.example.profis.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SwsCalculationService {

    private final WissenschaftlicheArbeitRepository thesisRepository;
    private final ReferentRepository professorRepository;
    private final StudentRepository studentRepository;
    private final StudiengangRepository programRepository;
    private final SemesterRepository semesterRepository;

    private final SwsCalculationConfig config = new SwsCalculationConfig();

    public SwsCalculationService(
            WissenschaftlicheArbeitRepository thesisRepository,
            ReferentRepository professorRepository,
            StudentRepository studentRepository,
            StudiengangRepository programRepository,
            SemesterRepository semesterRepository) {
        this.thesisRepository = thesisRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
        this.semesterRepository = semesterRepository;
    }

    public SwsCalculationReport calculateSwsForProfessor(Long professorId, Long semesterId) {
        try {
            // Get professor
            Referent professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new RuntimeException("Professor not found"));

            // Get all theses where this professor is involved
            List<WissenschaftlicheArbeit> theses = thesisRepository.findAll();

            // Filter theses for this professor and semester
            List<WissenschaftlicheArbeit> relevantTheses = filterThesesForProfessor(
                    theses, professorId, semesterId);

            // Calculate breakdown
            List<ThesisBreakdown> breakdown = new ArrayList<>();
            BigDecimal totalSws = BigDecimal.ZERO;
            BigDecimal swsAsSupervisor = BigDecimal.ZERO;
            BigDecimal swsAsCoSupervisor = BigDecimal.ZERO;
            int bachelorSupervisor = 0;
            int masterSupervisor = 0;
            int bachelorCoSupervisor = 0;
            int masterCoSupervisor = 0;

            for (WissenschaftlicheArbeit thesis : relevantTheses) {
                Student student = studentRepository.findById(thesis.getMatrikelnummer())
                        .orElse(null);

                // Determine thesis type based on program
                String thesisType = determineThesisType(student);

                // Calculate SWS based on role and type
                boolean isSupervisor = thesis.getIdErstreferent().equals(professorId);
                boolean isCoSupervisor = thesis.getIdKorreferent() != null &&
                        thesis.getIdKorreferent().equals(professorId);

                BigDecimal thisSws = BigDecimal.ZERO;
                String role = null;

                if (isSupervisor) {
                    role = "SUPERVISOR";
                    if ("BACHELOR".equals(thesisType)) {
                        thisSws = config.getBachelorSupervisorSws();
                        bachelorSupervisor++;
                    } else {
                        thisSws = config.getMasterSupervisorSws();
                        masterSupervisor++;
                    }
                    swsAsSupervisor = swsAsSupervisor.add(thisSws);
                } else if (isCoSupervisor) {
                    role = "CO_SUPERVISOR";
                    if ("BACHELOR".equals(thesisType)) {
                        thisSws = config.getBachelorCoSupervisorSws();
                        bachelorCoSupervisor++;
                    } else {
                        thisSws = config.getMasterCoSupervisorSws();
                        masterCoSupervisor++;
                    }
                    swsAsCoSupervisor = swsAsCoSupervisor.add(thisSws);
                }

                totalSws = totalSws.add(thisSws);

                // Create breakdown entry
                ThesisBreakdown breakdownEntry = new ThesisBreakdown();
                breakdownEntry.setThesisId(thesis.getIdWissenschaftlicheArbeiten());
                breakdownEntry.setThesisTitle(thesis.getTitel());
                breakdownEntry.setStudentName(student != null ? student.getVorname() + " " + student.getNachname() : "Unknown");
                breakdownEntry.setStudentMatriculationNumber(thesis.getMatrikelnummer());
                breakdownEntry.setRole(role);
                breakdownEntry.setThesisType(thesisType);
                breakdownEntry.setSws(thisSws);
                breakdownEntry.setStartDate(thesis.getStartDatum() != null ?
                        thesis.getStartDatum().toString() : null);
                breakdownEntry.setSubmissionDate(thesis.getAbgabeDatum() != null ?
                        thesis.getAbgabeDatum().toString() : null);

                // Add program info if available
                if (student != null) {
                    Studiengang program = programRepository.findById(student.getIdStudiengang())
                            .orElse(null);
                    if (program != null) {
                        breakdownEntry.setProgram(program.getTitelNachAbschluss());
                    }
                }

                breakdown.add(breakdownEntry);
            }

            // Calculate summary statistics
            SwsCalculation calculation = new SwsCalculation();
            calculation.setTotalSws(totalSws.setScale(2, RoundingMode.HALF_UP));
            calculation.setSwsAsSupervisor(swsAsSupervisor.setScale(2, RoundingMode.HALF_UP));
            calculation.setSwsAsCoSupervisor(swsAsCoSupervisor.setScale(2, RoundingMode.HALF_UP));
            calculation.setBachelorThesesAsSupervisor(bachelorSupervisor);
            calculation.setMasterThesesAsSupervisor(masterSupervisor);
            calculation.setBachelorThesesAsCoSupervisor(bachelorCoSupervisor);
            calculation.setMasterThesesAsCoSupervisor(masterCoSupervisor);
            calculation.setMaxSwsAllowed(config.getMaxSwsPerSemester());

            BigDecimal remaining = config.getMaxSwsPerSemester().subtract(totalSws);
            calculation.setSwsRemaining(remaining.setScale(2, RoundingMode.HALF_UP));
            calculation.setIsOverLimit(totalSws.compareTo(config.getMaxSwsPerSemester()) > 0);

            if (!relevantTheses.isEmpty()) {
                BigDecimal avgSws = totalSws.divide(
                        new BigDecimal(relevantTheses.size()),
                        3,
                        RoundingMode.HALF_UP);
                calculation.setAverageSwsPerThesis(avgSws);
            } else {
                calculation.setAverageSwsPerThesis(BigDecimal.ZERO);
            }

            // Build final report
            SwsCalculationReport report = new SwsCalculationReport();
            report.setProfessorId(professorId);
            report.setProfessorName(professor.getVorname() + " " + professor.getNachname());
            report.setSemesterId(semesterId);
            report.setSemesterName("WS 2024/2025"); // Would fetch from semester table
            report.setSwsCalculation(calculation);
            report.setBreakdown(breakdown);

            return report;

        } catch (SQLException e) {
            throw new RuntimeException("Error calculating SWS", e);
        }
    }

    private String determineThesisType(Student student) {
        if (student == null) {
            return "BACHELOR"; // default
        }

        try {
            // Get program and check degree type
            Studiengang program = programRepository.findById(student.getIdStudiengang())
                    .orElse(null);

            if (program != null) {
                // Check if the degree is a Master's degree
                // idArtAbschluss would need to be resolved to actual degree name
                // For example: 1 = Bachelor, 2 = Master
                // This is simplified - you'd need to check the actual ArtAbschluss table
                Long degreeTypeId = program.getIdArtAbschluss();
                if (degreeTypeId != null && degreeTypeId == 2L) {
                    return "MASTER";
                }
            }
        } catch (SQLException e) {
            // Log error and default to Bachelor
        }

        return "BACHELOR";
    }

    private List<WissenschaftlicheArbeit> filterThesesForProfessor(
            List<WissenschaftlicheArbeit> allTheses,
            Long professorId,
            Long semesterId) {

        List<WissenschaftlicheArbeit> filtered = new ArrayList<>();

        for (WissenschaftlicheArbeit thesis : allTheses) {
            boolean involvedAsSupervisor = thesis.getIdErstreferent() != null &&
                    thesis.getIdErstreferent().equals(professorId);
            boolean involvedAsCoSupervisor = thesis.getIdKorreferent() != null &&
                    thesis.getIdKorreferent().equals(professorId);

            if (involvedAsSupervisor || involvedAsCoSupervisor) {
                if (semesterId == null || thesisBelongsToSemester(thesis, semesterId)) {
                    filtered.add(thesis);
                }
            }
        }

        return filtered;
    }


    public boolean thesisBelongsToSemester(WissenschaftlicheArbeit thesis, Long semesterId) {
        if (thesis == null || semesterId == null) {
            return false;
        }

        try {
            Semester semester = semesterRepository.findById(semesterId)
                    .orElse(null);

            if (semester == null || semester.getStartDate() == null || semester.getEndDate() == null) {
                return false;
            }

            LocalDate semesterStart = semester.getStartDate();
            LocalDate semesterEnd = semester.getEndDate();

            LocalDate thesisStart = thesis.getStartDatum();
            LocalDate thesisSubmission = thesis.getAbgabeDatum();

            if (thesisStart == null && thesisSubmission == null) {
                return false;
            }

            if (thesisStart != null) {
                if (!thesisStart.isBefore(semesterStart) && !thesisStart.isAfter(semesterEnd)) {
                    return true;
                }
            }

            if (thesisSubmission != null) {
                if (!thesisSubmission.isBefore(semesterStart) && !thesisSubmission.isAfter(semesterEnd)) {
                    return true;
                }
            }

            if (thesisStart != null && thesisSubmission != null) {
                if (thesisStart.isBefore(semesterStart) && thesisSubmission.isAfter(semesterEnd)) {
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error checking semester for thesis: " + e.getMessage());
            return false;
        }
    }

    public SwsCalculationConfig getConfig() {
        return config;
    }

    public List<SwsCalculationReport> calculateSwsForAllProfessors(Long semesterId) {
        try {
            List<Referent> allProfessors = professorRepository.findAll();
            List<SwsCalculationReport> reports = new ArrayList<>();

            for (Referent professor : allProfessors) {
                SwsCalculationReport report = calculateSwsForProfessor(
                        professor.getIdReferent(),
                        semesterId);
                reports.add(report);
            }

            return reports;

        } catch (SQLException e) {
            throw new RuntimeException("Error calculating SWS for all professors", e);
        }
    }
}