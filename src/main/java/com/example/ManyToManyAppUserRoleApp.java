package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.manytomany.AppUser;
import com.example.manytomany.Role;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.Set;

@Slf4j
public class ManyToManyAppUserRoleApp {

    public static void main(String[] args) {
        try {

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{AppUser.class, Role.class}, Database.H2);
            sessionFactory.inTransaction(session -> {
                Role userRole = new Role("ROLE_USER", "ROLE USER DESCRIPTION");
                Role adminRole = new Role("ROLE_ADMIN", "ROLE ADMIN DESCRIPTION");

                session.persist(userRole);
                session.persist(adminRole);

                AppUser appUser1 = new AppUser("firstuser@gmgg.com", "firstuser", "firstuser", "firstuser");
                appUser1.addRole(userRole);
                appUser1.addRole(adminRole);
                session.persist(appUser1);
            });

            sessionFactory.inTransaction(session -> {
                AppUser appUser = session.createQuery("""
                                select u
                                from AppUser u
                                join fetch u.roles
                                where u.userId = :userId
                                """, AppUser.class)
                        .setParameter("userId", 1L)
                        .getSingleResult();
                Role role = session.find(Role.class, 2L);
                Set<Role> roles = appUser.getRoles();
                for (Role r : roles) {
                    if (r.equals(role)) {
                        roles.remove(r);
                    }
                }
                Role manager = new Role("ROLE_MANAGER", "ROLE MANAGER DESCRIPTION");
                session.persist(manager);
                roles.add(manager);
                log.info("AppUser :: {}", appUser);
            });

            sessionFactory.inTransaction(session -> {
                AppUser appUser = session.find(AppUser.class, 1L);
                session.remove(appUser);
            });
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
