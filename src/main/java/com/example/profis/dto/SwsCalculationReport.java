package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwsCalculationReport {
    private Long professorId;
    private String professorName;
    private Long semesterId;
    private String semesterName;
    private SwsCalculation swsCalculation;
    private List<ThesisBreakdown> breakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwsCalculation {
        private BigDecimal totalSws;
        private BigDecimal swsAsSupervisor;
        private BigDecimal swsAsCoSupervisor;
        private Integer bachelorThesesAsSupervisor;
        private Integer masterThesesAsSupervisor;
        private Integer bachelorThesesAsCoSupervisor;
        private Integer masterThesesAsCoSupervisor;
        private BigDecimal maxSwsAllowed;
        private BigDecimal swsRemaining;
        private Boolean isOverLimit;
        private BigDecimal averageSwsPerThesis;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThesisBreakdown {
        private Long thesisId;
        private String thesisTitle;
        private String studentName;
        private Long studentMatriculationNumber;
        private String role;
        private String thesisType;
        private BigDecimal sws;
        private String program;
        private String department;
        private String status;
        private String startDate;
        private String submissionDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwsCalculationConfig {
        private BigDecimal bachelorSupervisorSws = new BigDecimal("0.10");
        private BigDecimal bachelorCoSupervisorSws = new BigDecimal("0.05");
        private BigDecimal masterSupervisorSws = new BigDecimal("0.20");
        private BigDecimal masterCoSupervisorSws = new BigDecimal("0.10");
        private BigDecimal maxSwsPerSemester = new BigDecimal("2.0");
        private Boolean includeOngoingTheses = true;
    }
}