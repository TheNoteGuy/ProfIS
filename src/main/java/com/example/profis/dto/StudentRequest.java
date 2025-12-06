package com.example.profis.dto;

import lombok.Data;

@Data
public class StudentRequest {
    private String vorname;
    private String nachname;
    private String mail;
    private Boolean scheinfrei;
    private Long idStudiengang;
}