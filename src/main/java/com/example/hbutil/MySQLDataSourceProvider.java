package com.example.hbutil;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.hypersistence.utils.test.providers.AbstractContainerDataSourceProvider;
import io.hypersistence.utils.test.providers.DataSourceProvider;
import org.hibernate.dialect.Database;
import org.hibernate.dialect.MySQLDialect;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MySQLDataSourceProvider extends AbstractContainerDataSourceProvider {

    public static final DataSourceProvider INSTANCE = new MySQLDataSourceProvider();

    private boolean rewriteBatchedStatements = true;

    private boolean cachePrepStmts = false;

    private boolean useServerPrepStmts = false;

    private boolean useTimezone = false;

    private boolean useJDBCCompliantTimezoneShift = false;

    private boolean useLegacyDatetimeCode = true;

    public boolean isRewriteBatchedStatements() {
        return rewriteBatchedStatements;
    }

    public void setRewriteBatchedStatements(boolean rewriteBatchedStatements) {
        this.rewriteBatchedStatements = rewriteBatchedStatements;
    }

    public boolean isCachePrepStmts() {
        return cachePrepStmts;
    }

    public void setCachePrepStmts(boolean cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public boolean isUseServerPrepStmts() {
        return useServerPrepStmts;
    }

    public void setUseServerPrepStmts(boolean useServerPrepStmts) {
        this.useServerPrepStmts = useServerPrepStmts;
    }

    public boolean isUseTimezone() {
        return useTimezone;
    }

    public void setUseTimezone(boolean useTimezone) {
        this.useTimezone = useTimezone;
    }

    public boolean isUseJDBCCompliantTimezoneShift() {
        return useJDBCCompliantTimezoneShift;
    }

    public void setUseJDBCCompliantTimezoneShift(boolean useJDBCCompliantTimezoneShift) {
        this.useJDBCCompliantTimezoneShift = useJDBCCompliantTimezoneShift;
    }

    public boolean isUseLegacyDatetimeCode() {
        return useLegacyDatetimeCode;
    }

    public void setUseLegacyDatetimeCode(boolean useLegacyDatetimeCode) {
        this.useLegacyDatetimeCode = useLegacyDatetimeCode;
    }

    @Override
    public Database database() {
        return Database.MYSQL;
    }

    @Override
    public String hibernateDialect() {
        return MySQLDialect.class.getName();
    }

    @Override
    protected String defaultJdbcUrl() {
        return "jdbc:mysql://localhost:3306/testdb?createDatabaseIfNotExist=true";
    }

    protected DataSource newDataSource() {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL(url());
            dataSource.setUser(username());
            dataSource.setPassword(password());
            dataSource.setRewriteBatchedStatements(rewriteBatchedStatements);
            dataSource.setCachePrepStmts(cachePrepStmts);
            dataSource.setUseServerPrepStmts(useServerPrepStmts);

            return dataSource;
        } catch (SQLException e) {
            throw new IllegalStateException("The DataSource could not be instantiated!");
        }
    }

    @Override
    public String username() {
        return "root";
    }

    @Override
    public String password() {
        return "root";
    }

    @Override
    public String toString() {
        return "MySQLDataSourceProvider{" +
               "rewriteBatchedStatements=" + rewriteBatchedStatements +
               ", cachePrepStmts=" + cachePrepStmts +
               ", useServerPrepStmts=" + useServerPrepStmts +
               ", useTimezone=" + useTimezone +
               ", useJDBCCompliantTimezoneShift=" + useJDBCCompliantTimezoneShift +
               ", useLegacyDatetimeCode=" + useLegacyDatetimeCode +
               '}';
    }

    @Override
    public JdbcDatabaseContainer newJdbcDatabaseContainer() {
        return new MySQLContainer("mysql:8.0.40");
    }
}
