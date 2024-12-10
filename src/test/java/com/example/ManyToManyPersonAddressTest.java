package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.manytomany.Address;
import com.example.manytomany.Person;
import com.example.manytomany.PersonAddress;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class ManyToManyPersonAddressTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Person.class, Address.class, PersonAddress.class});
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
            log.info("Person Equals : {}", person1.equals(person2));

            Address address1 = new Address("12th Avenue", "12A", "4005A");
            Address address2 = new Address("18th Avenue", "18B", "4007B");
            log.info("Address Equals : {}", address1.equals(address2));

            session.persist(person1);
            session.persist(person2);

            session.persist(address1);
            session.persist(address2);

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
            PersonAddress address = person.getAddresses().get(0);
            person.removeAddress(address.getAddress());
            assertNotNull(person);
        });
    }
}
