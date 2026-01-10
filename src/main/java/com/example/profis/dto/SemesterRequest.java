package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRequest {
    private String name;
    private String type; // WINTER or SUMMER
    private LocalDate startDate;
    private LocalDate endDate;
}
