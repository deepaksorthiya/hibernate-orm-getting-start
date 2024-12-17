package com.example;

import com.example.model.Event;
import io.hypersistence.utils.test.AbstractHibernateTest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.BatchSettings;
import org.hibernate.cfg.MappingSettings;
import org.hibernate.dialect.Database;

import java.time.LocalDateTime;
import java.util.Properties;

@Slf4j
public class JpaEntityManagerFactoryApp extends AbstractHibernateTest {

    @Override
    protected Database database() {
        return Database.H2;
    }

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                Event.class
        };
    }

    @Override
    protected boolean connectionPooling() {
        return true;
    }

    @Override
    protected void additionalProperties(Properties settings) {
        settings.put(BatchSettings.ORDER_UPDATES, true);
        settings.put(BatchSettings.ORDER_INSERTS, true);
        settings.put(BatchSettings.STATEMENT_BATCH_SIZE, 20);
        settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(MappingSettings.PHYSICAL_NAMING_STRATEGY, io.hypersistence.utils.hibernate.naming.CamelCaseToSnakeCaseNamingStrategy.INSTANCE);
    }

    public void test() {
        init();
        doInJPA((entityManager -> {
            for (int i = 0; i < 100; i++) {
                entityManager.persist(new Event("A Film event" + i, LocalDateTime.now()));
            }
        }));
        destroy();
    }

    public static void main(String[] args) {
        new JpaEntityManagerFactoryApp().test();
    }
}
