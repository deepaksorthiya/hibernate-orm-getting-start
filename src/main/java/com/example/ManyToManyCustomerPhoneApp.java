package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.manytomany.Customer;
import com.example.manytomany.Phone;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

@Slf4j
public class ManyToManyCustomerPhoneApp {

    public static void main(String[] args) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Customer.class, Phone.class}, Database.H2);
            sessionFactory.inTransaction(session -> {
                Customer customer1 = new Customer("ABC-123");
                Customer customer2 = new Customer("DEF-456");

                Phone phone1 = new Phone("1111111111", "home");
                Phone phone2 = new Phone("18th Avenue", "cell");

                session.persist(phone1);
                session.persist(phone2);

                customer1.addPhone(phone1);
                customer1.addPhone(phone2);

                customer2.addPhone(phone1);

                session.persist(customer1);
                session.persist(customer2);
            });

            sessionFactory.inTransaction(session -> {
                log.info("Removing customer");
                Customer customer = session.find(Customer.class, 1);
                // this will remove only customer and customer_phone row
                session.remove(customer);
            });

            sessionFactory.inTransaction(session -> {
                log.info("Removing phone");
                Phone phone = session.find(Phone.class, 1);
                // this will remove phone, associated customer and customer_phone row
                session.remove(phone);
            });

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
