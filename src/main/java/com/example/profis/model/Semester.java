package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    private Long idSemester;
    private String name;
    private String type; // WINTER or SUMMER
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
