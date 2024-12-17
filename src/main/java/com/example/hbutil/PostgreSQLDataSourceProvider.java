package com.example.hbutil;

import io.hypersistence.utils.test.providers.AbstractContainerDataSourceProvider;
import io.hypersistence.utils.test.providers.DataSourceProvider;
import org.hibernate.dialect.Database;
import org.hibernate.dialect.PostgreSQLDialect;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

public class PostgreSQLDataSourceProvider extends AbstractContainerDataSourceProvider {

    public static final DataSourceProvider INSTANCE = new PostgreSQLDataSourceProvider();

    @Override
    public Database database() {
        return Database.POSTGRESQL;
    }

    @Override
    public String hibernateDialect() {
        return PostgreSQLDialect.class.getName();
    }

    @Override
    protected String defaultJdbcUrl() {
        return "jdbc:postgresql://localhost/testdb";
    }

    protected DataSource newDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url());
        dataSource.setUser(username());
        dataSource.setPassword(password());

        return dataSource;
    }

    @Override
    public String username() {
        return "postgres";
    }

    @Override
    public String password() {
        return "postgres";
    }

    @Override
    public JdbcDatabaseContainer newJdbcDatabaseContainer() {
        return new PostgreSQLContainer("postgres:16");
    }
}
