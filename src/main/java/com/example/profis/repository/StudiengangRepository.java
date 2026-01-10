package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.Studiengang;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudiengangRepository {

    private final Connection connection;

    public StudiengangRepository(Connection connection) {
        this.connection = connection;
    }

    public Studiengang save(Studiengang studiengang) throws SQLException {
        String sql = "INSERT INTO Studiengaenge (id_fachbereich, titel_nach_abschluss, id_art_abschluss) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setLong(ps, 1, studiengang.getIdFachbereich());
            DatabaseUtils.setString(ps, 2, studiengang.getTitelNachAbschluss());
            DatabaseUtils.setLong(ps, 3, studiengang.getIdArtAbschluss());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    studiengang.setIdStudiengang(generatedKeys.getLong(1));
                }
            }
        }

        return studiengang;
    }

    public Optional<Studiengang> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Studiengaenge WHERE id_studiengang = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudiengang(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Studiengang> findAll() throws SQLException {
        List<Studiengang> studiengaenge = new ArrayList<>();
        String sql = "SELECT * FROM Studiengaenge ORDER BY titel_nach_abschluss";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                studiengaenge.add(mapResultSetToStudiengang(rs));
            }
        }

        return studiengaenge;
    }

    public Studiengang update(Studiengang studiengang) throws SQLException {
        String sql = "UPDATE Studiengaenge SET id_fachbereich = ?, titel_nach_abschluss = ?, id_art_abschluss = ? WHERE id_studiengang = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setLong(ps, 1, studiengang.getIdFachbereich());
            DatabaseUtils.setString(ps, 2, studiengang.getTitelNachAbschluss());
            DatabaseUtils.setLong(ps, 3, studiengang.getIdArtAbschluss());
            ps.setLong(4, studiengang.getIdStudiengang());

            ps.executeUpdate();
        }

        return studiengang;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Studiengaenge WHERE id_studiengang = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Studiengaenge WHERE id_studiengang = ?";

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

    private Studiengang mapResultSetToStudiengang(ResultSet rs) throws SQLException {
        return new Studiengang(
                DatabaseUtils.getLong(rs, "id_studiengang"),
                DatabaseUtils.getLong(rs, "id_fachbereich"),
                DatabaseUtils.getString(rs, "titel_nach_abschluss"),
                DatabaseUtils.getLong(rs, "id_art_abschluss")
        );
    }
}