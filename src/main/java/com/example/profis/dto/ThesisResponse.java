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
public class ThesisResponse {
    private Long id;
    private Long studentMatriculationNumber;
    private String studentName;
    private Long supervisorId;
    private String supervisorName;
    private Long coSupervisorId;
    private String coSupervisorName;
    private String title;
    private String type;
    private String status;
    private LocalDate startDate;
    private LocalDate submissionDate;
    private LocalDate correctionEndDate;
    private LocalDateTime colloquiumStart;
    private LocalDateTime colloquiumEnd;
    private BigDecimal gradeSupervisor;
    private BigDecimal gradeCoSupervisor;
    private BigDecimal gradeColloquiumSupervisor;
    private BigDecimal gradeColloquiumCoSupervisor;
    private BigDecimal finalGrade;
}
