package com.kursova.dao;

import com.kursova.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavouriteTourDAO {

    public void insert(int tourId) throws SQLException {
        String sql = "INSERT INTO favourite_tour (tour_id) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tourId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM favourite_tour WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByTourId(int tourId) throws SQLException {
        String sql = "DELETE FROM favourite_tour WHERE tour_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tourId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TourEntity> getAll() {
        List<TourEntity> favourites = new ArrayList<>();
        String sql = "SELECT t.* FROM favourite_tour f JOIN tour t ON t.id = f.tour_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            TransportDAO transportDAO = new TransportDAO();

            while (rs.next()) {
                TransportEntity transport = transportDAO.getById(rs.getInt("transport_id"));

                TourEntity tour = new TourEntity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        transport,
                        rs.getString("meal_option"),
                        rs.getInt("number_of_days"),
                        rs.getInt("price"),
                        rs.getDouble("rating")
                );
                favourites.add(tour);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favourites;
    }
}
