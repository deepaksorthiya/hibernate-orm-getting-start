package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.model.Event;

import java.time.LocalDateTime;

public class EventApp {

    public static void main(String[] args) {

        try {
            HibernateUtil.getSessionFactory(new Class[]{Event.class}).inTransaction(session -> {
                session.persist(new Event("Our very first event!", LocalDateTime.now()));
                session.persist(new Event("A follow up event", LocalDateTime.now()));
                session.persist(new Event("A Film event", LocalDateTime.now()));
            });
        } finally {
            HibernateUtil.shutdown();
        }

    }
}
