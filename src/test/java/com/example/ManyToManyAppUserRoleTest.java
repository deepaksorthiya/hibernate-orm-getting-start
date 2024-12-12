package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.manytomany.AppUser;
import com.example.manytomany.Role;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class ManyToManyAppUserRoleTest {
    private static SessionFactory sessionFactory;


    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{AppUser.class, Role.class}, Database.H2);
        insertInitRecords();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    static void insertInitRecords() {
        sessionFactory.inTransaction(session -> {
            Role userRole = new Role("ROLE_USER", "ROLE USER DESCRIPTION");
            Role adminRole = new Role("ROLE_ADMIN", "ROLE ADMIN DESCRIPTION");

            session.persist(userRole);
            session.persist(adminRole);

            log.info("Role not equals : {}", userRole.equals(adminRole));

            AppUser appUser1 = new AppUser("firstuser@gmgg.com", "firstuser", "firstuser", "firstuser");
            appUser1.addRole(userRole);
            appUser1.addRole(adminRole);
            session.persist(appUser1);

            AppUser appUser2 = new AppUser("seconduser@gmgg.com", "seconduser", "seconduser", "seconduser");
            appUser2.addRole(userRole);
            appUser2.addRole(adminRole);
            session.persist(appUser2);

            AppUser appUser3 = new AppUser("thirduser@gmgg.com", "thirduser", "thirduser", "thirduser");
            appUser3.addRole(userRole);
            appUser3.addRole(adminRole);
            session.persist(appUser3);

            AppUser appUser4 = new AppUser("fourthuser@gmgg.com", "fourthuser", "fourthuser", "fourthuser");
            appUser4.addRole(userRole);
            appUser4.addRole(adminRole);
            session.persist(appUser4);
        });

    }

    @Test
    void getAppUserWithRoles() {
        log.info("Fetching Entity Start.........");
        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> {
            AppUser appUser = session.createQuery("""
                            select u
                            from AppUser u
                            join fetch u.roles
                            where u.userId = :userId
                            """, AppUser.class)
                    .setParameter("userId", 1L)
                    .getSingleResult();
            log.info("AppUser: {}", appUser);
            Set<Role> roles = appUser.getRoles();
            log.info("AppUser Roles: {}", roles);
            assertNotNull(roles);
            assertNotNull(appUser);
        });
        log.info("Fetching Entity End.........");
    }

    @Test
    void removeChildRole() {
        log.info("Removing ChildRole Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            AppUser appUser = session.createQuery("""
                            select u
                            from AppUser u
                            join fetch u.roles
                            where u.userId = :userId
                            """, AppUser.class)
                    .setParameter("userId", 1L)
                    .getSingleResult();
            Set<Role> roles = appUser.getRoles();
            roles.remove(roles.iterator().next());
            assertEquals(1, roles.size());
        });
        log.info("Removing ChildRole End.........");
    }

    @Test
    void removeChildRoleWrongWay() {
        log.info("Removing ChildRole Wrong Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            AppUser appUser = session.createQuery("""
                            select u
                            from AppUser u
                            join fetch u.roles
                            where u.userId = :userId
                            """, AppUser.class)
                    .setParameter("userId", 4L)
                    .getSingleResult();
            Set<Role> roles = appUser.getRoles();
            for (Role role : roles) {
                if ("ROLE_ADMIN".equals(role.getRoleName())) {
                    appUser.removeRole(role);
                    break;
                }
            }
            assertEquals(1, roles.size());
        });
        log.info("Removing ChildRole Wrong End.........");
    }

    @Test
    void removeAllChildRole() {
        log.info("Removing All ChildRole Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {

            AppUser appUser = session.createQuery("""
                            select u
                            from AppUser u
                            join fetch u.roles
                            where u.userId = :userId
                            """, AppUser.class)
                    .setParameter("userId", 2L)
                    .getSingleResult();
            appUser.setRoles(null);
        });
        log.info("Removing All ChildRole End.........");
    }

    @Test
    void testRemoveParent() {
        log.info("Removing Parent Start.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            AppUser appUser = session.find(AppUser.class, 3L);
            session.remove(appUser);
        });
        log.info("Removing Parent End.........");
    }
}