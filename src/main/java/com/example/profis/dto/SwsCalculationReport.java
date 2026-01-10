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
        private BigDecimal maxSwsAllowed;
        private BigDecimal swsRemaining;
        private Boolean isOverLimit;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThesisBreakdown {
        private Long thesisId;
        private String thesisTitle;
        private String role;
        private BigDecimal sws;
        private String program;
    }
}
