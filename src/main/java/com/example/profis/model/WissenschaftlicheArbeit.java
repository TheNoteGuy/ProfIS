package com.example.profis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WissenschaftlicheArbeit {
    private Long idWissenschaftlicheArbeiten;
    private Long matrikelnummer;
    private Long idErstreferent;
    private Long idKorreferent;
    private String titel;
    private LocalDate startDatum;
    private LocalDate abgabeDatum;
    private LocalDate endeKorrektur;
    private LocalDateTime kolloquimBeginn;
    private LocalDateTime kolloquimEnde;
    private BigDecimal noteArbeitReferent;
    private BigDecimal noteArbeitKorreferent;
    private BigDecimal noteKolloquiumReferent;
    private BigDecimal noteKolloquiumKorreferent;
}