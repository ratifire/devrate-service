package com.ratifire.devrate.configuration;

import jakarta.annotation.PostConstruct;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Configuration for adding new users to the table in a local environment.
 */
@Configuration
@Profile("local")
public class DatabaseInitializationConfig {

  private final DataSource dataSource;


  public DatabaseInitializationConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Add new users for frontend testing by executing SQL scripts after the application context has
   * been initialized. This method is annotated with @PostConstruct to ensure it is executed after
   * the initialization of the Spring application context. It creates a DatabasePopulator instance
   * and configures it with a ResourceDatabasePopulator that loads and executes the SQL script
   * "add_new_users_script.sql" located in the /db_scripts/add_new_users_script.sql. Finally, it
   * populates the database by calling the populate() method with the DataSource connection.
   *
   * @throws SQLException if there is an error accessing or executing SQL statements on the
   *                      database.
   */
  @PostConstruct
  public void initializeDatabase() throws SQLException {
    DatabasePopulator populator = new ResourceDatabasePopulator(
        new ClassPathResource("/db_scripts/add_new_users_script.sql")
    );

    populator.populate(dataSource.getConnection());
  }
}
