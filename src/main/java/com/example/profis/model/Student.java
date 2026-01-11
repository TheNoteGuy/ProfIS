package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long matrikelnummer;
    private final String vorname;
    private final String nachname;
    private final String mail;
    private final Boolean scheinfrei;
    private final Long idStudiengang;

    public Student(String vorname, String nachname, String mail, Boolean scheinfrei, Long idStudiengang) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.mail = mail;
        this.scheinfrei = scheinfrei;
        this.idStudiengang = idStudiengang;
    }
}