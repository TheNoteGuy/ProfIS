package com.example.profis.database;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatabaseUtils {
    
    // Getter-Methoden
    public static String getString(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }
    
    public static int getInt(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }
    
    public static Integer getIntegerNullable(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
    
    public static long getLong(ResultSet rs, String columnName) throws SQLException {
        return rs.getLong(columnName);
    }
    
    public static Long getLongNullable(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
    
    public static boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
        return rs.getBoolean(columnName);
    }
    
    public static Boolean getBooleanNullable(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        return rs.wasNull() ? null : value;
    }
    
    public static LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
        String dateStr = rs.getString(columnName);
        return dateStr != null ? LocalDate.parse(dateStr) : null;
    }
    
    public static LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        String timestampStr = rs.getString(columnName);
        return timestampStr != null ? LocalDateTime.parse(timestampStr.replace(" ", "T")) : null;
    }
    
    public static BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException {
        return rs.getBigDecimal(columnName);
    }
    
    // Setter-Methoden
    public static void setString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
        ps.setString(parameterIndex, value);
    }
    
    public static void setInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.INTEGER);
        }
    }
    
    public static void setLong(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            ps.setLong(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.INTEGER);
        }
    }
    
    public static void setBoolean(PreparedStatement ps, int parameterIndex, Boolean value) throws SQLException {
        if (value != null) {
            ps.setBoolean(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.INTEGER);
        }
    }
    
    public static void setLocalDate(PreparedStatement ps, int parameterIndex, LocalDate value) throws SQLException {
        ps.setString(parameterIndex, value != null ? value.toString() : null);
    }
    
    public static void setLocalDateTime(PreparedStatement ps, int parameterIndex, LocalDateTime value) throws SQLException {
        ps.setString(parameterIndex, value != null ? value.toString().replace("T", " ") : null);
    }
    
    public static void setBigDecimal(PreparedStatement ps, int parameterIndex, BigDecimal value) throws SQLException {
        if (value != null) {
            ps.setBigDecimal(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.DECIMAL);
        }
    }
}