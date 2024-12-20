package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.model.Event;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;

public class EventApp {

    public static void main(String[] args) {

        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Event.class}, Database.H2);
            sessionFactory.inTransaction(session -> {
                session.persist(new Event("Our very first event!", LocalDateTime.now()));
                session.persist(new Event("A follow up event", LocalDateTime.now()));
                session.persist(new Event("A Film event", LocalDateTime.now()));
            });

            sessionFactory.inTransaction(session -> {
                Event event = session.find(Event.class, 1);
                session.remove(event);
            });

        } finally {
            HibernateUtil.shutdown();
        }

    }
}
