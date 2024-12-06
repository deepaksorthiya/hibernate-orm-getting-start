package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;

@Slf4j
class HibernateSessionFactoryTest {
    private static SessionFactory sessionFactory;


    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Event.class});
        insertInitRecords();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    static void insertInitRecords() {
        // create a couple of events...
        sessionFactory.inTransaction(session -> {
            session.persist(new Event("Our very first event!", now()));
            session.persist(new Event("A follow up event", now()));
        });

    }

    @Test
    void insertEvent() {
        // create a couple of events...
        log.info("Adding Events.........");
        sessionFactory.inTransaction(session -> {
            session.persist(new Event("Our very first event!", now()));
            session.persist(new Event("A follow up event", now()));
        });
        log.info("Added Events............");
    }

    @Test
    void getEvents() {
        log.info("Fetching Events.........");
        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> session.createQuery("select e from Event e", Event.class).getResultList()
                .forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle())));
        log.info("Fetched Events.........");
    }

    @Test
    void removeEvent() {
        log.info("Removing Event.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            Event event = session.getReference(Event.class, 1);
            session.remove(event);
        });
        log.info("Removed Event.........");
    }
}