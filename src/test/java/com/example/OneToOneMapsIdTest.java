package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

//@Disabled
class OneToOneMapsIdTest {

    private static SessionFactory sessionFactory;


    @BeforeAll
    static void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Post.class, PostDetails.class}, Database.H2);
//        HypersistenceOptimizer hypersistenceOptimizer = new HypersistenceOptimizer(
//                new HibernateConfig(sessionFactory));
//        List<Event> events = hypersistenceOptimizer.getEvents();
//        System.out.println(events);
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testLifecycle() {
        sessionFactory.inTransaction(entityManager -> {
            Post post = new Post("First post");
            PostDetails details = new PostDetails("John Doe");
            details.setPost(post);
            entityManager.persist(details);
        });


        sessionFactory.inTransaction(entityManager -> {
            Post post = entityManager.find(Post.class, 1L);
            assertNotNull(post);
        });

        sessionFactory.inTransaction(entityManager -> {
            Post post = entityManager.find(Post.class, 1L);
            entityManager.remove(post);
        });
        sessionFactory.inTransaction(entityManager -> {
            PostDetails details = entityManager.find(PostDetails.class, 1L);
            System.out.println(details);
        });
    }

    @Test
    public void testNPlusOne() {
        sessionFactory.inTransaction(entityManager -> {
            Post post1 = new Post("First post");
            PostDetails details1 = new PostDetails("John Doe");
            details1.setPost(post1);
            Post post2 = new Post("Second post");
            PostDetails details2 = new PostDetails("John Doe");
            details2.setPost(post2);
            entityManager.persist(details1);
            entityManager.persist(details2);
        });
        sessionFactory.inTransaction(entityManager -> {
            List<Post> posts = entityManager.createQuery(
                            "select p " +
                            "from Post p ", Post.class)
                    .getResultList();
            posts.stream().forEach(System.out::println);
        });
    }

    @Test
    public void testNPlusOneWithJoinFetch() {
        sessionFactory.inTransaction(entityManager -> {
            Post post1 = new Post("First post");
            PostDetails details1 = new PostDetails("John Doe");
            details1.setPost(post1);
            Post post2 = new Post("Second post");
            PostDetails details2 = new PostDetails("John Doe");
            details2.setPost(post2);
            entityManager.persist(details1);
            entityManager.persist(details2);
        });
        sessionFactory.inTransaction(entityManager -> {
            entityManager.setDefaultReadOnly(true);
            List<PostDetails> posts = entityManager.createQuery(
                            """
                                    select p from PostDetails p join fetch p.post
                                    """, PostDetails.class)
                    .getResultList();
            posts.stream().forEach(P -> System.out.println(P.getPost()));
        });
    }

    @Entity(name = "Post")
    @Table(name = "post")
    @ToString
    public static class Post {

        @Id
        @GeneratedValue
        private Long id;

        private String title;

        public Post() {
        }

        public Post(String title) {
            this.title = title;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Entity(name = "PostDetails")
    @Table(name = "post_details")
    public static class PostDetails {

        @Id
        private Long id;

        @Column(name = "created_on")
        private Date createdOn;

        @Column(name = "created_by")
        private String createdBy;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "post_id")
        @OnDelete(action = OnDeleteAction.CASCADE)
        private Post post;

        public PostDetails() {
        }

        public PostDetails(String createdBy) {
            createdOn = new Date();
            this.createdBy = createdBy;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        @Override
        public String toString() {
            return "PostDetails{" +
                   "id=" + id +
                   ", createdOn=" + createdOn +
                   ", createdBy='" + createdBy + '\'' +
                   '}';
        }
    }
}
