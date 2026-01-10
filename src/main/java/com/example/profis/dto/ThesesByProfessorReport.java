package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThesesByProfessorReport {
    private Long professorId;
    private String professorName;
    private Long semesterId;
    private String semesterName;
    private Statistics statistics;
    private List<ThesisSummary> theses;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private Integer totalTheses;
        private Integer bachelorTheses;
        private Integer masterTheses;
        private Integer asSupervisor;
        private Integer asCoSupervisor;
        private Map<String, Integer> byStatus;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThesisSummary {
        private Long id;
        private String title;
        private String type;
        private String status;
        private String student;
        private String role;
    }
}
