//package com.ratifire.devrate;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import javax.sql.DataSource;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * This class provides integration tests.
// */
//@SpringBootTest
//public class H2IntegrationTest {
//
//  @Autowired
//  private DataSource dataSource;
//
//  @Test
//  public void testDataSource() throws SQLException {
//    try (Connection connection = dataSource.getConnection()) {
//      Assertions.assertNotNull(connection);
//      Assertions.assertFalse(connection.isClosed());
//    }
//  }
//}
