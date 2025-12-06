package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.Student;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private final Connection connection;

    public StudentRepository(Connection connection) {
        this.connection = connection;
    }

    public Student save(Student student) throws SQLException {
        String sql = "INSERT INTO Studenten (vorname, nachname, mail, scheinfrei, id_studiengang) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setString(ps, 1, student.getVorname());
            DatabaseUtils.setString(ps, 2, student.getNachname());
            DatabaseUtils.setString(ps, 3, student.getMail());
            DatabaseUtils.setBoolean(ps, 4, student.getScheinfrei());
            DatabaseUtils.setLong(ps, 5, student.getIdStudiengang());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setMatrikelnummer(generatedKeys.getLong(1));
                }
            }
        }

        return student;
    }

    public Optional<Student> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Studenten WHERE matrikelnummer = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Studenten ORDER BY nachname, vorname";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }

        return students;
    }

    public Student update(Student student) throws SQLException {
        String sql = "UPDATE Studenten SET vorname = ?, nachname = ?, mail = ?, scheinfrei = ?, id_studiengang = ? WHERE matrikelnummer = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, student.getVorname());
            DatabaseUtils.setString(ps, 2, student.getNachname());
            DatabaseUtils.setString(ps, 3, student.getMail());
            DatabaseUtils.setBoolean(ps, 4, student.getScheinfrei());
            DatabaseUtils.setLong(ps, 5, student.getIdStudiengang());
            ps.setLong(6, student.getMatrikelnummer());

            ps.executeUpdate();
        }

        return student;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Studenten WHERE matrikelnummer = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Studenten WHERE matrikelnummer = ?";

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

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                DatabaseUtils.getLong(rs, "matrikelnummer"),
                DatabaseUtils.getString(rs, "vorname"),
                DatabaseUtils.getString(rs, "nachname"),
                DatabaseUtils.getString(rs, "mail"),
                DatabaseUtils.getBooleanNullable(rs, "scheinfrei"),
                DatabaseUtils.getLong(rs, "id_studiengang")
        );
    }
}