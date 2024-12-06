package com.example.hbutil;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.BatchSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.tool.schema.Action;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static final List<Closeable> closeable = new ArrayList<>();

    private HibernateUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility method, cannot create object");
    }

    public static synchronized SessionFactory getSessionFactory(Class<?>[] classes) {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory(classes);
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
        for (Closeable closeable : closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("Failed {}", e.getMessage());
            }
        }
        closeable.clear();
    }

    private static SessionFactory buildSessionFactory(Class<?>[] classes) {
        StandardServiceRegistry standardRegistry = null;
        try {
            StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
            Map<String, Object> settings = new HashMap<>();
            settings.put(JdbcSettings.JAKARTA_JTA_DATASOURCE, getDataSource());
//            settings.put(JdbcSettings.SHOW_SQL, "true");
//            settings.put(JdbcSettings.FORMAT_SQL, "true");
//            settings.put(JdbcSettings.HIGHLIGHT_SQL, "true");
//            settings.put(JdbcSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
//            settings.put(StatisticsSettings.GENERATE_STATISTICS, true);
            settings.put(SchemaToolingSettings.HBM2DDL_AUTO, Action.ACTION_CREATE_THEN_DROP);
            settings.put(BatchSettings.ORDER_UPDATES, true);
            settings.put(BatchSettings.ORDER_INSERTS, true);
            settings.put(BatchSettings.STATEMENT_BATCH_SIZE, 20);
            settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            standardRegistryBuilder.applySettings(settings);
            standardRegistry = standardRegistryBuilder.build();

            // builds a session factory from the service registry
            final MetadataSources metadataSources = new MetadataSources(standardRegistry);

            for (Class<?> annotatedClass : classes) {
                metadataSources.addAnnotatedClass(annotatedClass);
            }

            Metadata metadata = metadataSources.getMetadataBuilder().build();
            return metadata.buildSessionFactory();
        } catch (Exception ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error("Initial SessionFactory creation failed. {}", ex.getMessage());
            // The registry would be destroyed by the SessionFactory, but we had
            // trouble
            // building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(standardRegistry);
            throw new ExceptionInInitializerError(ex.getCause());
        }
    }

    private static DataSource getDataSource() {
        // use pretty formatted query with multiline enabled
        PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);
        SystemOutQueryLoggingListener listener = new CustomSysLogger();
        listener.setQueryLogEntryCreator(creator);

        //actual datasource
        DataSource h2DataSource = getH2DataSource();

        // Create ProxyDataSource
        DataSource proxyDatasource = ProxyDataSourceBuilder.create(h2DataSource)
                .name("ProxyDataSource")
                .countQuery()
                .multiline()
                .listener(listener)
                .logSlowQueryToSysOut(1, TimeUnit.MINUTES)
                .build();
        return getHikariDataSource(proxyDatasource);
    }

    private static DataSource getHikariDataSource(DataSource dataSource) {

        HikariConfig config = new HikariConfig();
        config.setDataSource(dataSource);
        config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        // Maximum waiting time for a connection from the pool
        config.setConnectionTimeout(20000);
        // Minimum number of ideal connections in the pool
        config.setMinimumIdle(10);
        // Maximum number of actual connection in the pool
        config.setMaximumPoolSize(20);
        // Maximum time that a connection is allowed to sit ideal in the pool
        config.setIdleTimeout(300000);

        //Don't use AutoCommit
        config.setAutoCommit(false);

        HikariDataSource ds = new HikariDataSource(config);
        closeable.add(ds::close);
        return ds;
    }

    private static DataSource getH2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    private static class CustomSysLogger extends SystemOutQueryLoggingListener {
        private static final Set<String> skipQueriesToLog = Set.of(
                "create table",
                "drop table",
                "alter table",
                "create sequence",
                "drop sequence",
                "next value",
                "create global"
        );

        @Override
        protected void writeLog(String message) {
            for (String query : skipQueriesToLog) {
                if (message.contains(query)) {
                    return;
                }
            }
            super.writeLog(message);
        }
    }

    // use hibernate to format queries
    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private final Formatter formatter = FormatStyle.BASIC.getFormatter();

        @Override
        protected String formatQuery(String query) {
            return this.formatter.format(query);
        }
    }
}
