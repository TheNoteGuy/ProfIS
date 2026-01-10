package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.Fachbereich;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FachbereichRepository {

    private final Connection connection;

    public FachbereichRepository(Connection connection) {
        this.connection = connection;
    }

    public Fachbereich save(Fachbereich fachbereich) throws SQLException {
        String sql = "INSERT INTO Fachbereiche (name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setString(ps, 1, fachbereich.getName());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fachbereich.setIdFachbereich(generatedKeys.getLong(1));
                }
            }
        }

        return fachbereich;
    }

    public Optional<Fachbereich> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Fachbereiche WHERE id_fachbereich = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFachbereich(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Fachbereich> findAll() throws SQLException {
        List<Fachbereich> fachbereiche = new ArrayList<>();
        String sql = "SELECT * FROM Fachbereiche ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fachbereiche.add(mapResultSetToFachbereich(rs));
            }
        }

        return fachbereiche;
    }

    public Fachbereich update(Fachbereich fachbereich) throws SQLException {
        String sql = "UPDATE Fachbereiche SET name = ? WHERE id_fachbereich = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, fachbereich.getName());
            ps.setLong(2, fachbereich.getIdFachbereich());

            ps.executeUpdate();
        }

        return fachbereich;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Fachbereiche WHERE id_fachbereich = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Fachbereiche WHERE id_fachbereich = ?";

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

    private Fachbereich mapResultSetToFachbereich(ResultSet rs) throws SQLException {
        return new Fachbereich(
                DatabaseUtils.getLong(rs, "id_fachbereich"),
                DatabaseUtils.getString(rs, "name")
        );
    }
}