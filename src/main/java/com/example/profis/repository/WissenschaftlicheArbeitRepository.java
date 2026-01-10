package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.WissenschaftlicheArbeit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class WissenschaftlicheArbeitRepository {

    private final Connection connection;

    public WissenschaftlicheArbeitRepository(Connection connection) {
        this.connection = connection;
    }

    public WissenschaftlicheArbeit save(WissenschaftlicheArbeit arbeit) throws SQLException {
        String sql = """
            INSERT INTO Wissenschaftliche_Arbeiten 
            (matrikelnummer, id_erstreferent, id_korreferent, titel, start_datum, abgabe_datum, 
             ende_korrektur, kolloquim_beginn, kolloquim_ende, note_arbeit_referent, 
             note_arbeit_korreferent, note_kolloquium_referent, note_kolloquium_korreferent) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setLong(ps, 1, arbeit.getMatrikelnummer());
            DatabaseUtils.setLong(ps, 2, arbeit.getIdErstreferent());
            DatabaseUtils.setLong(ps, 3, arbeit.getIdKorreferent());
            DatabaseUtils.setString(ps, 4, arbeit.getTitel());
            DatabaseUtils.setLocalDate(ps, 5, arbeit.getStartDatum());
            DatabaseUtils.setLocalDate(ps, 6, arbeit.getAbgabeDatum());
            DatabaseUtils.setLocalDate(ps, 7, arbeit.getEndeKorrektur());
            DatabaseUtils.setLocalDateTime(ps, 8, arbeit.getKolloquimBeginn());
            DatabaseUtils.setLocalDateTime(ps, 9, arbeit.getKolloquimEnde());
            DatabaseUtils.setBigDecimal(ps, 10, arbeit.getNoteArbeitReferent());
            DatabaseUtils.setBigDecimal(ps, 11, arbeit.getNoteArbeitKorreferent());
            DatabaseUtils.setBigDecimal(ps, 12, arbeit.getNoteKolloquiumReferent());
            DatabaseUtils.setBigDecimal(ps, 13, arbeit.getNoteKolloquiumKorreferent());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    arbeit.setIdWissenschaftlicheArbeiten(generatedKeys.getLong(1));
                }
            }
        }

        return arbeit;
    }

    public Optional<WissenschaftlicheArbeit> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Wissenschaftliche_Arbeiten WHERE id_wissenschaftliche_arbeiten = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWissenschaftlicheArbeit(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<WissenschaftlicheArbeit> findAll() throws SQLException {
        List<WissenschaftlicheArbeit> arbeiten = new ArrayList<>();
        String sql = "SELECT * FROM Wissenschaftliche_Arbeiten ORDER BY start_datum DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                arbeiten.add(mapResultSetToWissenschaftlicheArbeit(rs));
            }
        }

        return arbeiten;
    }

    public WissenschaftlicheArbeit update(WissenschaftlicheArbeit arbeit) throws SQLException {
        String sql = """
            UPDATE Wissenschaftliche_Arbeiten SET 
            matrikelnummer = ?, id_erstreferent = ?, id_korreferent = ?, titel = ?, 
            start_datum = ?, abgabe_datum = ?, ende_korrektur = ?, kolloquim_beginn = ?, 
            kolloquim_ende = ?, note_arbeit_referent = ?, note_arbeit_korreferent = ?, 
            note_kolloquium_referent = ?, note_kolloquium_korreferent = ? 
            WHERE id_wissenschaftliche_arbeiten = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setLong(ps, 1, arbeit.getMatrikelnummer());
            DatabaseUtils.setLong(ps, 2, arbeit.getIdErstreferent());
            DatabaseUtils.setLong(ps, 3, arbeit.getIdKorreferent());
            DatabaseUtils.setString(ps, 4, arbeit.getTitel());
            DatabaseUtils.setLocalDate(ps, 5, arbeit.getStartDatum());
            DatabaseUtils.setLocalDate(ps, 6, arbeit.getAbgabeDatum());
            DatabaseUtils.setLocalDate(ps, 7, arbeit.getEndeKorrektur());
            DatabaseUtils.setLocalDateTime(ps, 8, arbeit.getKolloquimBeginn());
            DatabaseUtils.setLocalDateTime(ps, 9, arbeit.getKolloquimEnde());
            DatabaseUtils.setBigDecimal(ps, 10, arbeit.getNoteArbeitReferent());
            DatabaseUtils.setBigDecimal(ps, 11, arbeit.getNoteArbeitKorreferent());
            DatabaseUtils.setBigDecimal(ps, 12, arbeit.getNoteKolloquiumReferent());
            DatabaseUtils.setBigDecimal(ps, 13, arbeit.getNoteKolloquiumKorreferent());
            ps.setLong(14, arbeit.getIdWissenschaftlicheArbeiten());

            ps.executeUpdate();
        }

        return arbeit;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Wissenschaftliche_Arbeiten WHERE id_wissenschaftliche_arbeiten = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Wissenschaftliche_Arbeiten WHERE id_wissenschaftliche_arbeiten = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public List<WissenschaftlicheArbeit> findByMatrikelnummer(Long matrikelnummer) throws SQLException {
        List<WissenschaftlicheArbeit> arbeiten = new ArrayList<>();
        String sql = "SELECT * FROM Wissenschaftliche_Arbeiten WHERE matrikelnummer = ? ORDER BY start_datum DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, matrikelnummer);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    arbeiten.add(mapResultSetToWissenschaftlicheArbeit(rs));
                }
            }
        }

        return arbeiten;
    }

    public List<WissenschaftlicheArbeit> findByReferent(Long idReferent) throws SQLException {
        List<WissenschaftlicheArbeit> arbeiten = new ArrayList<>();
        String sql = "SELECT * FROM Wissenschaftliche_Arbeiten WHERE id_erstreferent = ? OR id_korreferent = ? ORDER BY start_datum DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, idReferent);
            ps.setLong(2, idReferent);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    arbeiten.add(mapResultSetToWissenschaftlicheArbeit(rs));
                }
            }
        }

        return arbeiten;
    }

    private WissenschaftlicheArbeit mapResultSetToWissenschaftlicheArbeit(ResultSet rs) throws SQLException {
        return new WissenschaftlicheArbeit(
                DatabaseUtils.getLong(rs, "id_wissenschaftliche_arbeiten"),
                DatabaseUtils.getLong(rs, "matrikelnummer"),
                DatabaseUtils.getLong(rs, "id_erstreferent"),
                DatabaseUtils.getLong(rs, "id_korreferent"),
                DatabaseUtils.getString(rs, "titel"),
                DatabaseUtils.getLocalDate(rs, "start_datum"),
                DatabaseUtils.getLocalDate(rs, "abgabe_datum"),
                DatabaseUtils.getLocalDate(rs, "ende_korrektur"),
                DatabaseUtils.getLocalDateTime(rs, "kolloquim_beginn"),
                DatabaseUtils.getLocalDateTime(rs, "kolloquim_ende"),
                DatabaseUtils.getBigDecimal(rs, "note_arbeit_referent"),
                DatabaseUtils.getBigDecimal(rs, "note_arbeit_korreferent"),
                DatabaseUtils.getBigDecimal(rs, "note_kolloquium_referent"),
                DatabaseUtils.getBigDecimal(rs, "note_kolloquium_korreferent")
        );
    }
}