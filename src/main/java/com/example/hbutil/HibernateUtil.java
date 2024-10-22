package com.example.hbutil;

import com.example.model.Event;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class HibernateUtil {

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    private HibernateUtil() {

    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder = getServiceRegistryBuilder();

                registry = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry);
                sources.addAnnotatedClass(Event.class);

                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (HibernateException e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    private static StandardServiceRegistryBuilder getServiceRegistryBuilder() {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        // Hibernate settings equivalent to hibernate.cfg.xml's properties
        Properties settings = getProperties();

        // HikariCP settings

        // Maximum waiting time for a connection from the pool
        settings.put("hibernate.hikari.connectionTimeout", "20000");
        // Minimum number of ideal connections in the pool
        settings.put("hibernate.hikari.minimumIdle", "10");
        // Maximum number of actual connection in the pool
        settings.put("hibernate.hikari.maximumPoolSize", "20");
        // Maximum time that a connection is allowed to sit ideal in the pool
        settings.put("hibernate.hikari.idleTimeout", "300000");

        registryBuilder.applySettings(settings);
        return registryBuilder;
    }

    private static Properties getProperties() {
        Properties settings = new Properties();
        settings.setProperty(Environment.JAKARTA_JDBC_DRIVER, "org.h2.Driver");
        settings.setProperty(Environment.JAKARTA_JDBC_URL, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        settings.setProperty(Environment.JAKARTA_JDBC_USER, "sa");
        settings.setProperty(Environment.JAKARTA_JDBC_PASSWORD, "");
        settings.setProperty(Environment.SHOW_SQL, "true");
        settings.setProperty(Environment.FORMAT_SQL, "true");
        settings.setProperty(Environment.HIGHLIGHT_SQL, "true");
        settings.setProperty(Environment.HBM2DDL_AUTO, "create");
        return settings;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
