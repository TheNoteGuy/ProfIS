package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Studiengang {
    private Long idStudiengang;
    private Long idFachbereich;
    private String titelNachAbschluss;
    private Long idArtAbschluss;
}