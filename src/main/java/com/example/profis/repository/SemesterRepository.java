package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.Semester;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SemesterRepository {

    private final Connection connection;

    public SemesterRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Semester> findAll() throws SQLException {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM Semester ORDER BY start_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                semesters.add(mapResultSetToSemester(rs));
            }
        }
        return semesters;
    }

    public Optional<Semester> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Semester WHERE id_semester = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSemester(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Findet das Semester, das ein bestimmtes Datum enthält
     */
    public Optional<Semester> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM Semester WHERE ? BETWEEN start_date AND end_date";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setLocalDate(ps, 1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSemester(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gibt das aktuell aktive Semester zurück
     */
    public Optional<Semester> findCurrentSemester() throws SQLException {
        String sql = "SELECT * FROM Semester WHERE is_current = 1";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Optional.of(mapResultSetToSemester(rs));
            }
        }
        return Optional.empty();
    }

    public Semester save(Semester semester) throws SQLException {
        String sql = "INSERT INTO Semester (name, type, start_date, end_date, is_current) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setString(ps, 1, semester.getName());
            DatabaseUtils.setString(ps, 2, semester.getType());
            DatabaseUtils.setLocalDate(ps, 3, semester.getStartDate());
            DatabaseUtils.setLocalDate(ps, 4, semester.getEndDate());
            ps.setBoolean(5, semester.getIsCurrent() != null ? semester.getIsCurrent() : false);

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    semester.setIdSemester(generatedKeys.getLong(1));
                }
            }
        }
        return semester;
    }

    public Semester update(Semester semester) throws SQLException {
        String sql = "UPDATE Semester SET name = ?, type = ?, start_date = ?, end_date = ?, is_current = ? " +
                "WHERE id_semester = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, semester.getName());
            DatabaseUtils.setString(ps, 2, semester.getType());
            DatabaseUtils.setLocalDate(ps, 3, semester.getStartDate());
            DatabaseUtils.setLocalDate(ps, 4, semester.getEndDate());
            ps.setBoolean(5, semester.getIsCurrent() != null ? semester.getIsCurrent() : false);
            ps.setLong(6, semester.getIdSemester());

            ps.executeUpdate();
        }
        return semester;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Semester WHERE id_semester = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Aktualisiert den "is_current" Status aller Semester basierend auf dem aktuellen Datum
     */
    public void updateCurrentStatus() throws SQLException {
        LocalDate now = LocalDate.now();
        String sql = "UPDATE Semester SET is_current = CASE " +
                "WHEN ? BETWEEN start_date AND end_date THEN 1 " +
                "ELSE 0 END";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setLocalDate(ps, 1, now);
            ps.executeUpdate();
        }
    }

    /**
     * Prüft ob ein Semester mit diesem Namen bereits existiert
     */
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Semester WHERE name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Semester mapResultSetToSemester(ResultSet rs) throws SQLException {
        Semester semester = new Semester();
        semester.setIdSemester(DatabaseUtils.getLong(rs, "id_semester"));
        semester.setName(DatabaseUtils.getString(rs, "name"));
        semester.setType(DatabaseUtils.getString(rs, "type"));
        semester.setStartDate(DatabaseUtils.getLocalDate(rs, "start_date"));
        semester.setEndDate(DatabaseUtils.getLocalDate(rs, "end_date"));
        semester.setIsCurrent(DatabaseUtils.getBooleanNullable(rs, "is_current"));
        return semester;
    }
}