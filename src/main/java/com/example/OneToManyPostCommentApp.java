package com.example;

import com.example.hbutil.Database;
import com.example.hbutil.HibernateUtil;
import com.example.onetomany.Post;
import com.example.onetomany.PostComment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

@Slf4j
public class OneToManyPostCommentApp {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory(new Class[]{Post.class, PostComment.class}, Database.H2);

    public static void main(String[] args) {
        savePost();
        getPost();
        removePostCommentOnly();
        removePostFully();
        HibernateUtil.shutdown();
    }

    private static void savePost() {

        sessionFactory.inTransaction(session -> session.persist(
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
        ));
    }

    private static void getPost() {
        sessionFactory.inTransaction(session -> {
            Post post = session.find(Post.class, 1L);
            PostComment comment = post.getComments().get(0);
            log.info("Comment :: {}", comment);
        });

    }

    private static void removePostCommentOnly() {
        sessionFactory.inTransaction(session -> {
            Post post = session.find(Post.class, 1L);
            PostComment comment = post.getComments().get(0);

            post.removeComment(comment);
        });
    }

    private static void removePostFully() {
        sessionFactory.inTransaction(session -> {
            Post post = session.find(Post.class, 1L);
            session.remove(post);
        });
    }
}
