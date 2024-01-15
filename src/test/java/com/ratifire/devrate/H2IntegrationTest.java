package com.ratifire.devrate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootTest
public class H2IntegrationTest {

    private static final String DB_URL = "jdbc:h2:mem:testdb";

    @Test
    public void testH2MemoryConnection() {

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            Assertions.assertNotNull(connection);
            Assertions.assertFalse(connection.isClosed());
        } catch (SQLException ex) {
            System.out.println("Establishing database connection: " + ex.getMessage());
        }
    }
}
