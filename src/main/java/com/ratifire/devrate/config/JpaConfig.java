package com.ratifire.devrate.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    @Bean(name = "H2DataSource")
    public DataSource h2DataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:testing");
        dataSourceBuilder.username("test");
        dataSourceBuilder.password("test");
        return dataSourceBuilder.build();
    }
}
