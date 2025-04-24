package com.kursova.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportDAO {
    private final Connection connection;

    public TransportDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(TransportEntity transport) {
        String sql = "INSERT INTO transport (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, transport.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(TransportEntity transport) {
        String sql = "UPDATE transport SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, transport.getName());
            stmt.setInt(2, transport.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM transport WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TransportEntity> getAll() {
        List<TransportEntity> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport";

        try (Statement stmt = connection.createStatement();
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

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
