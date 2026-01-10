package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.Referent;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReferentRepository {

    private final Connection connection;

    public ReferentRepository(Connection connection) {
        this.connection = connection;
    }

    public Referent save(Referent referent) throws SQLException {
        String sql = "INSERT INTO Referenten (kommentar, anrede, vorname, nachname, ist_extern, telefon, mail, addresse) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setString(ps, 1, referent.getKommentar());
            DatabaseUtils.setString(ps, 2, referent.getAnrede());
            DatabaseUtils.setString(ps, 3, referent.getVorname());
            DatabaseUtils.setString(ps, 4, referent.getNachname());
            DatabaseUtils.setBoolean(ps, 5, referent.getIstExtern());
            DatabaseUtils.setString(ps, 6, referent.getTelefon());
            DatabaseUtils.setString(ps, 7, referent.getMail());
            DatabaseUtils.setString(ps, 8, referent.getAddresse());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    referent.setIdReferent(generatedKeys.getLong(1));
                }
            }
        }

        return referent;
    }

    public Optional<Referent> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Referenten WHERE id_referent = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReferent(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Referent> findAll() throws SQLException {
        List<Referent> referenten = new ArrayList<>();
        String sql = "SELECT * FROM Referenten ORDER BY nachname, vorname";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                referenten.add(mapResultSetToReferent(rs));
            }
        }

        return referenten;
    }

    public Referent update(Referent referent) throws SQLException {
        String sql = "UPDATE Referenten SET kommentar = ?, anrede = ?, vorname = ?, nachname = ?, ist_extern = ?, telefon = ?, mail = ?, addresse = ? WHERE id_referent = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, referent.getKommentar());
            DatabaseUtils.setString(ps, 2, referent.getAnrede());
            DatabaseUtils.setString(ps, 3, referent.getVorname());
            DatabaseUtils.setString(ps, 4, referent.getNachname());
            DatabaseUtils.setBoolean(ps, 5, referent.getIstExtern());
            DatabaseUtils.setString(ps, 6, referent.getTelefon());
            DatabaseUtils.setString(ps, 7, referent.getMail());
            DatabaseUtils.setString(ps, 8, referent.getAddresse());
            ps.setLong(9, referent.getIdReferent());

            ps.executeUpdate();
        }

        return referent;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Referenten WHERE id_referent = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Referenten WHERE id_referent = ?";

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

    private Referent mapResultSetToReferent(ResultSet rs) throws SQLException {
        return new Referent(
                DatabaseUtils.getLong(rs, "id_referent"),
                DatabaseUtils.getString(rs, "kommentar"),
                DatabaseUtils.getString(rs, "anrede"),
                DatabaseUtils.getString(rs, "vorname"),
                DatabaseUtils.getString(rs, "nachname"),
                DatabaseUtils.getBooleanNullable(rs, "ist_extern"),
                DatabaseUtils.getString(rs, "telefon"),
                DatabaseUtils.getString(rs, "mail"),
                DatabaseUtils.getString(rs, "addresse")
        );
    }
}