package com.example;

import com.example.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static java.time.LocalDateTime.now;

public class JpaPersistenceXmlApp {

    public static void main(String[] args) {
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa")) {

            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.persist(new Event("Our very first event!", now()));
                entityManager.persist(new Event("A follow up event", now()));
                transaction.commit();
            } catch (Exception e) {
                throw e;
            }

        }
    }
}
