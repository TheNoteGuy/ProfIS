package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRequest {
    private Long departmentId;
    private String degreeTitle;
    private Long degreeTypeId;
}
