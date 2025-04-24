package com.kursova.dao;

import com.kursova.entity.TourEntity;
import com.kursova.entity.TransportEntity;
import com.kursova.testutil.TestDatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TourDAOTest {

    private Connection connection;
    private TourDAO tourDAO;
    private TransportDAO transportDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseConnection.getConnection();
        tourDAO = new TourDAO(connection);
        transportDAO = new TransportDAO(connection);
        setupSchema();
    }

    private void setupSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS tour");
            stmt.execute("DROP TABLE IF EXISTS transport");

            stmt.execute("""
                CREATE TABLE transport(
                    id   SERIAL PRIMARY KEY,
                    name VARCHAR(255) UNIQUE NOT NULL
                );
                """);

            stmt.execute("""
                CREATE TABLE tour
                (
                    id             SERIAL PRIMARY KEY,
                    name           VARCHAR(255),
                    type           VARCHAR(50),
                    transport_id   INT REFERENCES transport(id),
                    meal_option    VARCHAR(50),
                    number_of_days INT,
                    price          INT,
                    rating         DOUBLE PRECISION
                )
                """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close(); // H2 in-memory база самознищується
    }

    @Test
    void testInsertAndGetAll() {
        transportDAO.insert(new TransportEntity("Bus"));
        TransportEntity transport = transportDAO.getById(1);
        tourDAO.insert(new TourEntity(
                "Safari Adventure",
                "Adventure",
                transport,
                "Full Board",
                7,
                1200,
                4.8
        ));
        List<TourEntity> all = tourDAO.getAll();
        assertEquals(1, all.size());
        TourEntity tour = all.getFirst();

        assertEquals("Safari Adventure", tour.getName());
        assertEquals("Adventure", tour.getType());
        assertEquals(transport.getId(), tour.getTransportId());
        assertEquals("Full Board", tour.getMealOption());
        assertEquals(7, tour.getNumberOfDays());
        assertEquals(1200, tour.getPrice());
        assertEquals(4.8, tour.getRating());
    }

    @Test
    void testGetById() {
        transportDAO.insert(new TransportEntity("Bus"));
        TransportEntity transport = transportDAO.getById(1);
        tourDAO.insert(new TourEntity(
                "Safari Adventure",
                "Adventure",
                transport,
                "Full Board",
                7,
                1200,
                4.8
        ));
        TourEntity found = tourDAO.getAll().getFirst();
        TourEntity byId = tourDAO.getById(found.getId());
        assertNotNull(byId);
        assertEquals(found.getId(), byId.getId());
    }

    @Test
    void testUpdate() {
        transportDAO.insert(new TransportEntity("Bus"));
        TransportEntity transport = transportDAO.getById(1);
        tourDAO.insert(new TourEntity(
                "Safari Adventure",
                "Adventure",
                transport,
                "Full Board",
                7,
                1200,
                4.8
        ));
        TourEntity tour = tourDAO.getById(1);

        tour.setName("Safari Adventure UPD");
        tourDAO.update(tour);

        TourEntity updated = tourDAO.getById(tour.getId());
        assertEquals("Safari Adventure UPD", updated.getName());
    }

    @Test
    void testUpdateNonExistingId_shouldNotThrowButDoNothing() {
        TransportEntity transport = new TransportEntity("Bus");
        TourEntity tour = new TourEntity(
                9999,
                "Safari Adventure",
                "Adventure",
                transport,
                "Full Board",
                7,
                1200,
                4.8
        );
        tourDAO.update(tour);

        TourEntity result = tourDAO.getById(tour.getId());
        assertNull(result);
    }

    @Test
    void testDelete() {
        transportDAO.insert(new TransportEntity("Bus"));
        TransportEntity transport = transportDAO.getById(1);
        tourDAO.insert(new TourEntity("Adventure", "Adventure", transport, "Full Board", 7, 1200, 4.8));
        TourEntity tour = tourDAO.getAll().getFirst();

        tourDAO.delete(tour.getId());
        TourEntity deleted = tourDAO.getById(tour.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteNonExistingId_shouldNotThrow() {
        assertDoesNotThrow(() -> tourDAO.delete(12345));
    }

    @Test
    void testGetFiltered() {
        transportDAO.insert(new TransportEntity("Plane"));
        TransportEntity transport = transportDAO.getById(1);

        tourDAO.insert(new TourEntity("Relax Tour", "Relax", transport, "BB", 5, 500, 4.2));
        tourDAO.insert(new TourEntity("Shop Tour", "Shopping", transport, "HB", 3, 200, 4.5));
        tourDAO.insert(new TourEntity("Extreme Tour", "Adventure", transport, "FB", 10, 1500, 5.0));

        List<TourEntity> filtered = tourDAO.getFiltered(
                "Adventure",   // type
                "Plane",       // transportName
                null,          // mealOption (any)
                5, 12,         // minDays, maxDays
                500, 2000,     // minPrice, maxPrice
                4.0, 5.0       // minRating, maxRating
        );

        assertEquals(1, filtered.size());
        assertEquals("Extreme Tour", filtered.getFirst().getName());
    }

    @Test
    void testGetSorted_byPriceDesc() {
        transportDAO.insert(new TransportEntity("Plane"));
        TransportEntity transport = transportDAO.getById(1);

        tourDAO.insert(new TourEntity("Cheap Tour", "Relax", transport, "BB", 5, 300, 4.0));
        tourDAO.insert(new TourEntity("Medium Tour", "Relax", transport, "BB", 5, 800, 4.5));
        tourDAO.insert(new TourEntity("Expensive Tour", "Relax", transport, "BB", 5, 1500, 4.9));

        List<TourEntity> sorted = tourDAO.getSorted("price", false); // DESC

        assertEquals(3, sorted.size());
        assertEquals("Expensive Tour", sorted.getFirst().getName());
        assertEquals("Medium Tour", sorted.get(1).getName());
        assertEquals("Cheap Tour", sorted.get(2).getName());
    }
}
