package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.manytomany.Address;
import com.example.manytomany.Person;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ManyToManyPersonAddressTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Person.class, Address.class});
        insertInitRecords();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    static void insertInitRecords() {
        sessionFactory.inTransaction(session -> {
            Person person1 = new Person("ABC-123");
            Person person2 = new Person("DEF-456");

            Address address1 = new Address("12th Avenue", "12A", "4005A");
            Address address2 = new Address("18th Avenue", "18B", "4007B");

            person1.addAddress(address1);
            person1.addAddress(address2);

            person2.addAddress(address1);

            session.persist(person1);
            session.persist(person2);
        });
    }

    @Test
    void removeAddressFromPerson() {
        sessionFactory.inTransaction(session -> {
            Person person = session.get(Person.class, 1);
            Address address = person.getAddresses().get(0);
            person.removeAddress(address);
        });
    }
}
