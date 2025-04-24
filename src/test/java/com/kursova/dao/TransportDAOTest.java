package com.kursova.dao;

import com.kursova.testutil.TestDatabaseConnection;
import com.kursova.entity.TransportEntity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransportDAOTest {

    private Connection connection;
    private TransportDAO transportDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseConnection.getConnection();
        transportDAO = new TransportDAO(connection);
        setupSchema();
    }

    private void setupSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transport");
            stmt.execute("""
                CREATE TABLE transport(
                    id   SERIAL PRIMARY KEY,
                    name VARCHAR(255) UNIQUE NOT NULL
                );
                """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close(); // H2 in-memory база самознищується
    }

    @Test
    void testInsertAndGetAll() {
        transportDAO.insert(new TransportEntity("Автобус"));
        List<TransportEntity> all = transportDAO.getAll();
        assertEquals(1, all.size());
        assertEquals("Автобус", all.getFirst().getName());
    }

    @Test
    void testInsertDuplicate_shouldNotCrashButPrintError(){
        TransportEntity transport = new TransportEntity("Метро");
        transportDAO.insert(transport);
        transportDAO.insert(transport);

        List<TransportEntity> list = transportDAO.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById() {
        transportDAO.insert(new TransportEntity("Літак"));
        TransportEntity found = transportDAO.getAll().getFirst();
        TransportEntity byId = transportDAO.getById(found.getId());
        assertNotNull(byId);
        assertEquals("Літак", byId.getName());
    }

    @Test
    void testUpdate() {
        transportDAO.insert(new TransportEntity("Поїзд"));
        TransportEntity transport = transportDAO.getAll().getFirst();

        transport.setName("Швидкісний поїзд");
        transportDAO.update(transport);

        TransportEntity updated = transportDAO.getById(transport.getId());
        assertEquals("Швидкісний поїзд", updated.getName());
    }

    @Test
    void testUpdateNonExistingId_shouldNotThrowButDoNothing() {
        TransportEntity ghost = new TransportEntity(9999, "Привид");
        transportDAO.update(ghost);

        TransportEntity result = transportDAO.getById(9999);
        assertNull(result);
    }

    @Test
    void testDelete() {
        transportDAO.insert(new TransportEntity("Метро"));
        TransportEntity transport = transportDAO.getAll().getFirst();

        transportDAO.delete(transport.getId());
        TransportEntity deleted = transportDAO.getById(transport.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteNonExistingId_shouldNotThrow() {
        assertDoesNotThrow(() -> transportDAO.delete(12345));
    }
}
