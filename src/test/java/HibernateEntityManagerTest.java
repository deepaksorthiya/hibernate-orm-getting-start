import com.example.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static java.lang.System.out;
import static java.time.LocalDateTime.now;

public class HibernateEntityManagerTest {
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    protected static void setUp() {
        // an EntityManagerFactory is set up once for an application
        // IMPORTANT: notice how the name here matches the name we
        // gave the persistence-unit in persistence.xml
        entityManagerFactory = createEntityManagerFactory("org.hibernate.tutorial.jpa");
    }

    @AfterAll
    protected static void tearDown() {
        entityManagerFactory.close();
    }

    @Test
    public void testBasicUsage() {
        // create a couple of events...
        inTransaction(entityManager -> {
            entityManager.persist(new Event("Our very first event!", now()));
            entityManager.persist(new Event("A follow up event", now()));
        });

        // now lets pull events from the database and list them
        inTransaction(entityManager -> entityManager.createQuery("select e from Event e", Event.class).getResultList()
                .forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle())));
    }

    void inTransaction(Consumer<EntityManager> work) {
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

}