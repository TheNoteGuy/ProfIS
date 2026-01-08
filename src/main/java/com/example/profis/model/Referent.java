package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referent {
    private Long idReferent;
    private String kommentar;
    private String anrede;
    private String vorname;
    private String nachname;
    private Boolean istExtern;
    private String telefon;
    private String mail;
    private String addresse;
}