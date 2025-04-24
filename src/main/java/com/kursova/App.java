package com.kursova;

import com.kursova.dao.TransportDAO;
import com.kursova.dao.TransportEntity;

import java.sql.SQLException;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws SQLException {
        TransportDAO dao = new TransportDAO();
        dao.insert(new TransportEntity("Bus"));

        List<TransportEntity> allTransports = dao.getAll();
        for (TransportEntity t : allTransports) {
            System.out.println(t.getName());
        }
    }
}
