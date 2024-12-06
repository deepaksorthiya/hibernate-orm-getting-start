package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.onetone.ContactInfo;
import com.example.onetone.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class OneToOneUserProfileContactInfoTest {
    private static SessionFactory sessionFactory;


    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class});
        insertInitRecords();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    static void insertInitRecords() {
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile1 = new UserProfile(null, "firstUser", "firstUser", "firstUser@gmail.com", null);
            ContactInfo contactInfo1 = new ContactInfo(null, "1111111", "432 Ab", null);
            userProfile1.setDetails(contactInfo1);
            session.persist(userProfile1);

            UserProfile userProfile2 = new UserProfile(null, "secondUser", "secondUser", "secondUser@gmail.com", null);
            ContactInfo contactInfo2 = new ContactInfo(null, "222222", "432 Ab", null);
            userProfile2.setDetails(contactInfo2);
            session.persist(userProfile2);
        });

    }

    @Test
    void getUserProfileWithContactInfo() {
        log.info("Fetching Entity Start.........");
        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.find(UserProfile.class, 1L);
            ContactInfo contactInfo = userProfile.getContactInfo();
            assertNotNull(contactInfo);
            assertEquals("1111111", contactInfo.getPhoneNumber());
        });
        log.info("Fetching Entity End.........");
    }

    @Test
    void removeChildContactInfo() {
        log.info("Removing ChildContactInfo Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.find(UserProfile.class, 2L);
            log.info("UserProfile: {}", userProfile);
            if (userProfile != null) {
                ContactInfo contactInfo = userProfile.getContactInfo();
                if (contactInfo != null) {
                    // only remove if exist otherwise exception
                    session.remove(contactInfo);
                }
            }
        });
        log.info("Removing ChildContactInfo End.........");
    }

    @Test
    void testRemoveParent() {
        log.info("Removing Parent Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.find(UserProfile.class, 1L);
            session.remove(userProfile);
        });
        log.info("Removing Parent End.........");
    }
}