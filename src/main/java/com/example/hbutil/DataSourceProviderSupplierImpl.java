package com.example.hbutil;

import io.hypersistence.utils.test.providers.DataSourceProvider;
import io.hypersistence.utils.test.providers.DataSourceProviderSupplier;
import org.hibernate.dialect.Database;

import java.util.Map;

/**
 * @author Vlad Mihalcea
 */
public class DataSourceProviderSupplierImpl implements DataSourceProviderSupplier {

    @Override
    public Map<Database, DataSourceProvider> get() {
        return Map.of(
                //Database.ORACLE, OracleDataSourceProvider.INSTANCE,
                //Database.HSQL, HSQLDBDataSourceProvider.INSTANCE,
                //Database.SQLSERVER, SQLServerDataSourceProvider.INSTANCE,
                Database.H2, H2DataSourceProvider.INSTANCE,
                Database.MYSQL, MySQLDataSourceProvider.INSTANCE,
                Database.POSTGRESQL, PostgreSQLDataSourceProvider.INSTANCE
        );
    }
}
