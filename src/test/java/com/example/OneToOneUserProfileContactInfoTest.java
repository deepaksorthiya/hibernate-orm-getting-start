package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.onetone.ContactInfo;
import com.example.onetone.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ReadOnlyMode;
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
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class}, Database.H2);
//        HypersistenceOptimizer hypersistenceOptimizer = new HypersistenceOptimizer(
//                new HibernateConfig(sessionFactory));
//        List<Event> events = hypersistenceOptimizer.getEvents();
//        System.out.println(events);
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
            userProfile1.setContactDetails(contactInfo1);
            session.persist(userProfile1);

            UserProfile userProfile2 = new UserProfile(null, "secondUser", "secondUser", "secondUser@gmail.com", null);
            ContactInfo contactInfo2 = new ContactInfo(null, "222222", "432 Ab", null);
            userProfile2.setContactDetails(contactInfo2);
            session.persist(userProfile2);

            UserProfile userProfile3 = new UserProfile(null, "thirdUser", "thirdUser", "thirdUser@gmail.com", null);
            ContactInfo contactInfo3 = new ContactInfo(null, "3333333", "432 Ab", null);
            userProfile3.setContactDetails(contactInfo3);
            session.persist(userProfile3);

            UserProfile userProfile4 = new UserProfile(null, "fourthUser", "fourthUser", "fourthUser@gmail.com", null);
            ContactInfo contactInfo4 = new ContactInfo(null, "4444444", "432 Ab", null);
            userProfile4.setContactDetails(contactInfo4);
            session.persist(userProfile4);
        });

    }

    @Test
    void getUserProfileWithContactInfo() {
        log.info("Fetching Entity Start.........");
        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.createQuery(
                            """
                                    select u
                                    from UserProfile u
                                    join fetch u.contactInfo c
                                    where u.id = :id""", UserProfile.class)
                    .setParameter("id", 1L)
                    .setReadOnly(true)
                    .getSingleResultOrNull();
            ContactInfo contactInfo = userProfile.getContactInfo();
            assertNotNull(contactInfo);
            assertEquals("1111111", contactInfo.getPhoneNumber());
        });
        log.info("Fetching Entity End.........");
    }

    @Test
    void removeChildContactInfoAndUserProfile() {
        log.info("Removing ChildContactInfo Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.createQuery(
                            """
                                    select u
                                    from UserProfile u
                                    join fetch u.contactInfo c
                                    where u.id = :id""", UserProfile.class)
                    .setParameter("id", 2L)
                    .setReadOnly(true)
                    .getSingleResult();
            log.info("UserProfile: {}", userProfile);
            if (userProfile != null) {
                ContactInfo contactInfo = userProfile.getContactInfo();
                if (contactInfo != null) {
                    // only remove if exist otherwise exception
                    session.remove(userProfile);
                }
            }
        });
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.find(UserProfile.class, 2L, ReadOnlyMode.READ_ONLY);
            assertEquals(null, userProfile);
        });
        log.info("Removing ChildContactInfo End.........");
    }

    @Test
    void removeParentUserProfileAndContactInfo() {
        log.info("Removing Parent UserProfile and ContactInfo Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            ContactInfo contactInfo = session.createQuery(
                            """
                                    select c
                                    from ContactInfo c
                                    join fetch c.userProfile u
                                    where c.id = :id""", ContactInfo.class)
                    .setParameter("id", 4L)
                    .setReadOnly(true)
                    .getSingleResult();
            log.info("ContactInfo: {}", contactInfo);
            if (contactInfo != null) {
                UserProfile userProfile = contactInfo.getUserProfile();
                if (userProfile != null) {
                    // only remove if exist otherwise exception
                    session.remove(userProfile);
                }
            }
        });
        sessionFactory.inTransaction(session -> {
            ContactInfo contactInfo = session.find(ContactInfo.class, 4L, ReadOnlyMode.READ_ONLY);
            assertEquals(null, contactInfo);
        });
        log.info("Removing Parent UserProfile and ContactInfo End.........");
    }

    @Test
    void removeChildContactInfoWithoutRemoveAndSettingNull() {
        log.info("Removing ChildContactInfo Without Remove Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.createQuery(
                            """
                                    select u
                                    from UserProfile u
                                    join fetch u.contactInfo c
                                    where u.id = :id""", UserProfile.class)
                    .setParameter("id", 3L)
                    .setReadOnly(true)
                    .getSingleResultOrNull();
            assertNotNull(userProfile);
            userProfile.setContactDetails(null);
        });
        log.info("Removing ChildContactInfo Without Remove End.........");
    }

    @Test
    void testRemoveParent() {
        log.info("Removing Parent Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            UserProfile userProfile = session.find(UserProfile.class, 1L, ReadOnlyMode.READ_ONLY);
            session.remove(userProfile);
        });
        log.info("Removing Parent End.........");
    }
}