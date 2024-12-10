package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.onetone.ContactInfo;
import com.example.onetone.UserProfile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneToOneApp {

    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class}).inTransaction(session -> {
                UserProfile userProfile = new UserProfile(null, "firstUser", "firstUser", "firstuser@gmail.com", null);
                ContactInfo contactInfo = new ContactInfo(null, "999999", "432 Ab", null);
                userProfile.setContactDetails(contactInfo);
                session.persist(userProfile);
            });

            HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class}).inTransaction(session -> {
                UserProfile userProfile = session.find(UserProfile.class, 1L);
                log.info("UserProfile: {}", userProfile);
                if (userProfile != null) {
                    ContactInfo contactInfo = userProfile.getContactInfo();
                    if (contactInfo != null) {
                        // only remove if exist otherwise exception
                        session.remove(contactInfo);
                    }
                }
            });
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
