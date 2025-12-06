package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long matrikelnummer;
    private String vorname;
    private String nachname;
    private String mail;
    private Boolean scheinfrei;
    private Long idStudiengang;
}