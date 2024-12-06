package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.onetone.ContactInfo;
import com.example.onetone.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OneToOneMultiThreadingApp {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class});
        try {
            executor = Executors.newFixedThreadPool(100);
            for (int i = 0; i < 100; i++) {
                executor.submit(() -> {
                    System.out.println("###############" + Thread.currentThread().getName() + " Started###########");
                    sessionFactory.inTransaction(session -> {
                        UserProfile userProfile = new UserProfile(null, "firstUser", "firstUser", "firstuser@gmail.com", null);
                        session.persist(userProfile);
                    });
                    System.out.println("###############" + Thread.currentThread().getName() + " Finished###########");
                });
            }
            Thread.sleep(10000);
            sessionFactory.inTransaction(session -> session.createQuery("select e from UserProfile e", UserProfile.class).getResultList()
                    .forEach(userProfile -> System.out.println("UserProfile (" + userProfile.getFirstName() + ") : " + userProfile.getLastName())));
        } finally {
            assert executor != null;
            shutdownAndAwaitTermination(executor);
            HibernateUtil.shutdown();
        }
    }

    static void shutdownAndAwaitTermination(ExecutorService pool) {
        // Disable new tasks from being submitted
        pool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks forcefully
                pool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

}
