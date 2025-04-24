package com.kursova.dao;

import com.kursova.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportDAO {

    public void insert(TransportEntity transport) throws SQLException {
        String sql = "INSERT INTO transport (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transport.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(TransportEntity transport) throws SQLException {
        String sql = "UPDATE transport SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transport.getName());
            stmt.setInt(2, transport.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM transport WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TransportEntity> getAll() throws SQLException {
        List<TransportEntity> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TransportEntity transport = new TransportEntity(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                transports.add(transport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transports;
    }

    public TransportEntity getById(int id) {
        String sql = "SELECT * FROM transport WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TransportEntity(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
