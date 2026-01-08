package com.example.profis.repository;

import com.example.profis.database.DatabaseUtils;
import com.example.profis.model.ArtAbschluss;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtAbschlussRepository {

    private final Connection connection;

    public ArtAbschlussRepository(Connection connection) {
        this.connection = connection;
    }

    public ArtAbschluss save(ArtAbschluss artAbschluss) throws SQLException {
        String sql = "INSERT INTO Art_Abschluesse (name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DatabaseUtils.setString(ps, 1, artAbschluss.getName());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    artAbschluss.setIdArtAbschluss(generatedKeys.getLong(1));
                }
            }
        }

        return artAbschluss;
    }

    public Optional<ArtAbschluss> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM Art_Abschluesse WHERE id_art_abschluss = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToArtAbschluss(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<ArtAbschluss> findAll() throws SQLException {
        List<ArtAbschluss> abschluesse = new ArrayList<>();
        String sql = "SELECT * FROM Art_Abschluesse ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                abschluesse.add(mapResultSetToArtAbschluss(rs));
            }
        }

        return abschluesse;
    }

    public ArtAbschluss update(ArtAbschluss artAbschluss) throws SQLException {
        String sql = "UPDATE Art_Abschluesse SET name = ? WHERE id_art_abschluss = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            DatabaseUtils.setString(ps, 1, artAbschluss.getName());
            ps.setLong(2, artAbschluss.getIdArtAbschluss());

            ps.executeUpdate();
        }

        return artAbschluss;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Art_Abschluesse WHERE id_art_abschluss = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Art_Abschluesse WHERE id_art_abschluss = ?";

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

    private ArtAbschluss mapResultSetToArtAbschluss(ResultSet rs) throws SQLException {
        return new ArtAbschluss(
                DatabaseUtils.getLong(rs, "id_art_abschluss"),
                DatabaseUtils.getString(rs, "name")
        );
    }
}