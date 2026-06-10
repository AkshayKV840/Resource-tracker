package com.company.resourcetracker.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    /**
     * Render provides DATABASE_URL in the form:
     *   postgresql://user:password@host:port/dbname
     * (port is often omitted, defaulting to 5432).
     *
     * The PostgreSQL JDBC driver cannot parse credentials embedded in the URL,
     * so we split them into separate jdbcUrl / username / password values.
     */
    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            // Local fallback
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:postgresql://localhost:5432/resource_tracker");
            ds.setUsername("postgres");
            ds.setPassword("password");
            ds.setDriverClassName("org.postgresql.Driver");
            return ds;
        }

        URI uri = URI.create(databaseUrl);

        String userInfo = uri.getUserInfo();          // user:password
        String username = userInfo.split(":")[0];
        String password = userInfo.split(":").length > 1 ? userInfo.split(":")[1] : "";

        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMinimumIdle(2);
        ds.setMaximumPoolSize(10);
        return ds;
    }
}
