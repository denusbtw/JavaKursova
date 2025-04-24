package com.kursova.testutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class TestDatabaseConnection {

    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        String uniqueDbName = UUID.randomUUID().toString(); // генерує унікальну базу
        String url = "jdbc:h2:mem:" + uniqueDbName + ";DB_CLOSE_DELAY=-1";
        return DriverManager.getConnection(url, USER, PASSWORD);
    }
}
