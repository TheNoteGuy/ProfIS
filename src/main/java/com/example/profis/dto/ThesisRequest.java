package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThesisRequest {
    private Long matrikelnummer;
    private Long supervisorId;
    private Long coSupervisorId;
    private String title;
    private String type; // BACHELOR or MASTER
    private String status; // PLANNED, IN_PROGRESS, SUBMITTED, GRADED, COMPLETED
    private LocalDate startDate;
    private LocalDate submissionDate;
    private LocalDate correctionEndDate;
    private LocalDateTime colloquiumStart;
    private LocalDateTime colloquiumEnd;
    private BigDecimal gradeSupervisor;
    private BigDecimal gradeCoSupervisor;
    private BigDecimal gradeColloquiumSupervisor;
    private BigDecimal gradeColloquiumCoSupervisor;
}
