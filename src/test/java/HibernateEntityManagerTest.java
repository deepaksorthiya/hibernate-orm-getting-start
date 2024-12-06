import com.example.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static java.lang.System.out;
import static java.time.LocalDateTime.now;

@Slf4j
class HibernateEntityManagerTest {
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setUp() {
        // an EntityManagerFactory is set up once for an application
        // IMPORTANT: notice how the name here matches the name we
        // gave the persistence-unit in persistence.xml
        entityManagerFactory = createEntityManagerFactory("org.hibernate.tutorial.jpa");
        //entityManagerFactory = HibernateUtil.getSessionFactory(new Class[]{Event.class}).unwrap(EntityManagerFactory.class);
        insertInitRecords();

    }

    static void insertInitRecords() {
        // create a couple of events...
        inTransaction(entityManager -> {
            entityManager.persist(new Event("Our very first event!", now()));
            entityManager.persist(new Event("A follow up event", now()));
        });

    }

    @Test
    void insertEvent() {
        // create a couple of events...
        log.info("Adding Events.........");
        inTransaction(entityManager -> {
            entityManager.persist(new Event("Our very first event!", now()));
            entityManager.persist(new Event("A follow up event", now()));
        });
        log.info("Added Events............");
    }

    @Test
    public void getEvents() {
        log.info("Fetching Events.........");
        // now lets pull events from the database and list them
        inTransaction(entityManager -> entityManager.createQuery("select e from Event e", Event.class).getResultList()
                .forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle())));
        log.info("Fetched Events.........");
    }

    @Test
    void removeEvent() {
        log.info("Removing Event.........");
        //remove event
        inTransaction(entityManager -> {
            Event event = entityManager.find(Event.class, 1);
            entityManager.remove(event);
        });
        log.info("Removed Event.........");
    }

    static void inTransaction(Consumer<EntityManager> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try (entityManager) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            work.accept(entityManager);
            transaction.commit();
        } catch (Exception e) {
            throw e;
        }
    }

    @AfterAll
    static void tearDown() {
        entityManagerFactory.close();
    }

}