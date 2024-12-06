package com.example.hbutil;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.tool.schema.Action;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HibernateUtil {
    private static volatile SessionFactory sessionFactory;
    private static final List<Closeable> closeable = new ArrayList<>();

    private HibernateUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility method, cannot create object");
    }

    public static SessionFactory getSessionFactory(Class<?>[] classes) {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) { //double lock checking
                    sessionFactory = buildSessionFactory(classes);
                }
            }
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
                System.err.println("Failed" + e);
            }
        }
        closeable.clear();
    }

    private static SessionFactory buildSessionFactory(Class<?>[] classes) {
        StandardServiceRegistry standardRegistry = null;
        try {
            StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.JAKARTA_JTA_DATASOURCE, getDataSource());
            //settings.put(Environment.SHOW_SQL, "true");
            //settings.put(Environment.FORMAT_SQL, "true");
            //settings.put(Environment.HIGHLIGHT_SQL, "true");
            //settings.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
            //settings.put(Environment.GENERATE_STATISTICS, true);
            settings.put(Environment.HBM2DDL_AUTO, Action.ACTION_CREATE_THEN_DROP);
            settings.put(Environment.ORDER_UPDATES, true);
            settings.put(Environment.ORDER_INSERTS, true);
            settings.put(Environment.STATEMENT_BATCH_SIZE, 20);
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

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
            System.err.println(
                    "Initial SessionFactory creation failed." + ex.getMessage());
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
        @Override
        protected void writeLog(String message) {
            if (message != null && (message.contains("drop table") || message.contains("create table") || message.contains("alter table")
                    || message.contains("next value")) || message.contains("drop sequence") || message.contains("create sequence") || message.contains("create global")) {
                return;
            }
            super.writeLog(message);
        }
    }

    // use hibernate to format queries
    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private Formatter formatter = FormatStyle.BASIC.getFormatter();

        @Override
        protected String formatQuery(String query) {
            return this.formatter.format(query);
        }
    }
}
