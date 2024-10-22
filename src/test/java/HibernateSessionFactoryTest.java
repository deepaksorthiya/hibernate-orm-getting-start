import com.example.hbutil.HibernateUtil;
import com.example.model.Event;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;

public class HibernateSessionFactoryTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    protected static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    protected static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void basicUsage() {
        // create a couple of events...
        sessionFactory.inTransaction(session -> {
            session.persist(new Event("Our very first event!", now()));
            session.persist(new Event("A follow up event", now()));
        });

        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> session.createSelectionQuery("from Event", Event.class).getResultList()
                .forEach(event -> out.println("com.example.model.Event (" + event.getDate() + ") : " + event.getTitle())));
    }
}