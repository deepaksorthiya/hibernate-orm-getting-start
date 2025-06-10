package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.onetomany.Post;
import com.example.onetomany.PostComment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class OneToManyPostCommentTest {
    private static SessionFactory sessionFactory;


    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Post.class, PostComment.class}, Database.H2);
        insertInitRecords();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    static void insertInitRecords() {
        // create a couple of events...
        sessionFactory.inTransaction(session -> {
            session.persist(
                    new Post()
                            .setId(1L)
                            .setTitle("High-Performance Java Persistence")
                            .addComment(
                                    new PostComment()
                                            .setReview("Best book on JPA and Hibernate!")
                            )
                            .addComment(
                                    new PostComment()
                                            .setReview("A must-read for every Java developer!")
                            )
            );

            session.persist(
                    new Post()
                            .setId(2L)
                            .setTitle("JPA Persistence")
                            .addComment(
                                    new PostComment()
                                            .setReview("Best book on JPA and Hibernate!")
                            )
                            .addComment(
                                    new PostComment()
                                            .setReview("A must-read for every Java developer!")
                            )
            );
        });

    }

    @Test
    void getPostWithComments() {
        log.info("Fetching Post With Comments.........");
        // now lets pull events from the database and list them
        sessionFactory.inTransaction(session -> {
            Post post = session.createQuery("""
                            select p
                            from Post p
                            join fetch p.comments
                            where p.id = :id
                            """, Post.class)
                    .setParameter("id", 2L)
                    .getSingleResult();
            assertEquals(2, post.getComments().size());
        });
        log.info("Fetched Post With Comments.........");
    }

    @Test
    void removePostComment() {
        log.info("Removing Post Comment.........");
        //remove event
        sessionFactory.inTransaction(session -> {
            Post post = session.getReference(Post.class, 1L);
            PostComment comment = post.getComments().get(1);

            post.removeComment(comment);
        });
        log.info("Removed Post Comment.........");
    }

    @Test
    void testRemoveParent() {
        sessionFactory.inTransaction(entityManager -> {
            Post post = entityManager.createQuery("""
                            select p
                            from Post p
                            join fetch p.comments
                            where p.id = :id
                            """, Post.class)
                    .setParameter("id", 1L)
                    .getSingleResult();
            assertNotNull(post);
            entityManager.remove(post);
        });
    }
}