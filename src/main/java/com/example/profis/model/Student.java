package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long matrikelnummer;
    private String vorname;
    private String nachname;
    private String mail;
    private Boolean scheinfrei;
    private Long idStudiengang;

    // Konstruktor ohne Matrikelnummer (für neue Studenten)
    public Student(String vorname, String nachname, String mail, Boolean scheinfrei, Long idStudiengang) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.mail = mail;
        this.scheinfrei = scheinfrei;
        this.idStudiengang = idStudiengang;
    }
}