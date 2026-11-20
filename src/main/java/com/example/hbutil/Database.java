package com.example.hbutil;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public enum Database {

    H2 {
        @Override
        DataSource getDataSource() {
            // Disable the default fetch size for H2 to avoid memory issues with large result sets
            // for hypersistence-optimizer error fix
            System.setProperty("h2.serverResultSetFetchSize", "0");
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
            ds.setUser("sa");
            ds.setPassword("");
            return ds;
        }
    },
    MYSQL {
        @Override
        DataSource getDataSource() {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setURL("jdbc:mysql://localhost:3306/testdb?createDatabaseIfNotExist=true");
            ds.setUser("root");
            ds.setPassword("root");
            return ds;
        }
    },
    POSTGRESQL {
        @Override
        DataSource getDataSource() {
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setURL("jdbc:postgresql://localhost:5432/testdb");
            ds.setUser("postgres");
            ds.setPassword("postgres");
            ds.setReWriteBatchedInserts(true);
            return ds;
        }
    };

    abstract DataSource getDataSource();
}
