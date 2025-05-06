package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.manytomany.Address;
import com.example.manytomany.Person;
import com.example.manytomany.PersonAddress;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

@Slf4j
public class ManyToManyPersonAddressApp {

    public static void main(String[] args) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Person.class, Address.class, PersonAddress.class}, Database.H2);
            sessionFactory.inTransaction(session -> {
                Person person1 = new Person("ABC-123");
                Person person2 = new Person("DEF-456");

                Address address1 = new Address("12th Avenue", "12A", "4005A");
                Address address2 = new Address("18th Avenue", "18B", "4007B");

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

            sessionFactory.inTransaction(session -> {
                log.info("Removing address");
                int count = session.createMutationQuery("""
                                delete from PersonAddress pa 
                                where pa.person.id=:id and pa.address.id=:id
                                """)
                        .setParameter("id", 1L)
                        .executeUpdate();
                log.info("Count: {}", count);
            });

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
