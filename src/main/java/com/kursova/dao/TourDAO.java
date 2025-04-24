package com.kursova.dao;

import com.kursova.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDAO {
    public void insert(TourEntity tour) {
        String sql = "INSERT INTO tour (name, type, transport_id, meal_option, number_of_days, price, rating) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tour.getName());
            stmt.setString(2, tour.getType());
            stmt.setInt(3, tour.getTransportId());
            stmt.setString(4, tour.getMealOption());
            stmt.setInt(5, tour.getNumberOfDays());
            stmt.setInt(6, tour.getPrice());
            stmt.setDouble(7, tour.getRating());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(TourEntity tour) {
        String sql = "UPDATE tour SET name = ?, type = ?, transport_id = ?, meal_option = ?, number_of_days = ?, price = ?, rating = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tour.getName());
            stmt.setString(2, tour.getType());
            stmt.setInt(3, tour.getTransportId());
            stmt.setString(4, tour.getMealOption());
            stmt.setInt(5, tour.getNumberOfDays());
            stmt.setInt(6, tour.getPrice());
            stmt.setDouble(7, tour.getRating());
            stmt.setInt(8, tour.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tour WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TourEntity> getAll() {
        List<TourEntity> tours = new ArrayList<>();
        String sql = "SELECT * FROM tour";

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
                tours.add(tour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tours;
    }

    public TourEntity getById(int id) {
        String sql = "SELECT * FROM tour WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TransportDAO transportDAO = new TransportDAO();
                TransportEntity transport = transportDAO.getById(rs.getInt("transport_id"));

                return new TourEntity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        transport,
                        rs.getString("meal_option"),
                        rs.getInt("number_of_days"),
                        rs.getInt("price"),
                        rs.getDouble("rating")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TourEntity> getFiltered(
            String type,
            String transportName,
            String mealOption,
            int minDays,
            int maxDays,
            int minPrice,
            int maxPrice,
            double minRating,
            double maxRating
    ) {
        List<TourEntity> tours = new ArrayList<>();

        String sql = """
            SELECT t.* FROM tour t
            JOIN transport tr ON t.transport_id = tr.id
            WHERE (? IS NULL OR t.type = ?)
              AND (? IS NULL OR tr.name = ?)
              AND (? IS NULL OR t.meal_option = ?)
              AND t.number_of_days >= ?
              AND t.number_of_days <= ?
              AND t.price >= ?
              AND t.price <= ?
              AND t.rating >= ?
              AND t.rating <= ?
            ORDER BY t.id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            stmt.setString(2, type);
            stmt.setString(3, transportName);
            stmt.setString(4, transportName);
            stmt.setString(5, mealOption);
            stmt.setString(6, mealOption);
            stmt.setInt(7, minDays);
            stmt.setInt(8, maxDays);
            stmt.setInt(9, minPrice);
            stmt.setInt(10, maxPrice);
            stmt.setDouble(11, minRating);
            stmt.setDouble(12, maxRating);

            ResultSet rs = stmt.executeQuery();
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
                tours.add(tour);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tours;
    }

    public List<TourEntity> getSorted(
            String sortBy, // "price", "rating", "number_of_days"
            boolean ascending // true = ASC, false = DESC
    ) {
        List<TourEntity> tours = new ArrayList<>();

        List<String> allowedFields = List.of("price", "rating", "number_of_days");
        if (!allowedFields.contains(sortBy)) {
            sortBy = "price";
        }

        String direction = ascending ? "ASC" : "DESC";

        String sql = "SELECT * FROM tour ORDER BY " + sortBy + " " + direction;

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
                tours.add(tour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tours;
    }
}
