package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponse {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private String degreeTitle;
    private Long degreeTypeId;
    private String degreeTypeName;
}
