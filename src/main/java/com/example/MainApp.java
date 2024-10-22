package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.model.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import static java.time.LocalDateTime.now;

public class MainApp {

    public static void main(String[] args) {
        saveCustomer();
        getCustomer();
        HibernateUtil.shutdown();
    }

    private static void saveCustomer() {
        Session session = null;
        Transaction transaction = null;
        SessionFactory sessionFactory;
        try {
            sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Event event = new Event("Our very first event!", now());
            session.persist(event);

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    private static void getCustomer() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Event event = session.find(Event.class, 1L);
            System.out.println(event);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
