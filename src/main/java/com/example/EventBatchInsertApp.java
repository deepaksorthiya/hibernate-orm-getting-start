package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.model.Event;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class EventBatchInsertApp {

    public static void main(String[] args) {

        try {
            long startTime = System.currentTimeMillis();
            // batch insert
            HibernateUtil.getSessionFactory(new Class[]{Event.class}, Database.H2).inTransaction(session -> {
                for (int i = 1; i <= 60; i++) {
                    session.persist(new Event(i + " event!", LocalDateTime.now()));
                }
            });
            long endTime = System.currentTimeMillis();
            log.info("Total execution time: {}", (endTime - startTime));

            // batch update
            HibernateUtil.getSessionFactory(new Class[]{Event.class}, Database.H2).inTransaction(session -> {
                for (int i = 1; i <= 60; i++) {
                    Event event = session.find(Event.class, i);
                    event.setTitle("Event::" + i);
                }
            });

            HibernateUtil.getSessionFactory(new Class[]{Event.class}, Database.H2).inTransaction(session -> {
                List<Event> events = session.createQuery(
                        """
                                SELECT e FROM Event e
                                """, Event.class).getResultList();
                log.info("Events Size : {}", events.size());
            });
        } finally {
            HibernateUtil.shutdown();
        }

    }
}
