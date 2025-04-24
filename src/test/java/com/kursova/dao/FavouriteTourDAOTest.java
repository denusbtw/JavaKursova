package com.kursova.dao;

import com.kursova.entity.TourEntity;
import com.kursova.entity.TransportEntity;
import com.kursova.testutil.TestDatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FavouriteTourDAOTest {

    private Connection connection;
    private TourDAO tourDAO;
    private TransportDAO transportDAO;
    private FavouriteTourDAO favouriteTourDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseConnection.getConnection();
        tourDAO = new TourDAO(connection);
        transportDAO = new TransportDAO(connection);
        favouriteTourDAO = new FavouriteTourDAO(connection);
        setupSchema();
    }

    private void setupSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS favourite_tour");
            stmt.execute("DROP TABLE IF EXISTS tour");
            stmt.execute("DROP TABLE IF EXISTS transport");

            stmt.execute("""
            CREATE TABLE transport (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) UNIQUE NOT NULL
            );
        """);

            stmt.execute("""
            CREATE TABLE tour (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255),
                type VARCHAR(50),
                transport_id INT REFERENCES transport(id),
                meal_option VARCHAR(50),
                number_of_days INT,
                price INT,
                rating DOUBLE PRECISION
            );
        """);

            stmt.execute("""
            CREATE TABLE favourite_tour (
                id SERIAL PRIMARY KEY,
                tour_id INT REFERENCES tour(id)
            );
        """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private TourEntity createTour(String name) {
        TransportEntity transport = new TransportEntity("TestTransport");
        transportDAO.insert(transport);
        TransportEntity fetchedTransport = transportDAO.getAll().getFirst();

        TourEntity tour = new TourEntity(name, "Relax", fetchedTransport, "BB", 5, 500, 4.5);
        tourDAO.insert(tour);
        return tourDAO.getAll().getFirst();
    }

    @Test
    void testInsertAndGetAll() {
        TourEntity tour = createTour("Favourite Tour");

        favouriteTourDAO.insert(tour.getId());

        List<TourEntity> favourites = favouriteTourDAO.getAll();
        assertEquals(1, favourites.size());
        assertEquals("Favourite Tour", favourites.getFirst().getName());
    }

    @Test
    void testDeleteByTourId() {
        TourEntity tour = createTour("Removable Tour");

        favouriteTourDAO.insert(tour.getId());
        favouriteTourDAO.deleteByTourId(tour.getId());

        List<TourEntity> favourites = favouriteTourDAO.getAll();
        assertTrue(favourites.isEmpty());
    }

    @Test
    void testDeleteNonExistingId_shouldNotThrow() {
        assertDoesNotThrow(() -> favouriteTourDAO.delete(9999));
    }
}
