package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.onetone.ContactInfo;
import com.example.onetone.UserProfile;

public class OneToOneApp {

    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class}).inTransaction(session -> {
                UserProfile userProfile = new UserProfile(null, "firstUser", "firstUser", "firstuser@gmail.com", null);
                ContactInfo contactInfo = new ContactInfo(null, "999999", "432 Ab", null);
                userProfile.setDetails(contactInfo);
                session.persist(userProfile);
            });

            HibernateUtil.getSessionFactory(new Class[]{UserProfile.class, ContactInfo.class}).inTransaction(session -> {
                UserProfile userProfile = session.find(UserProfile.class, 1L);
                ContactInfo contactInfo = userProfile.getContactInfo();
                System.out.println(contactInfo);
                session.remove(userProfile.getContactInfo());
            });
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
