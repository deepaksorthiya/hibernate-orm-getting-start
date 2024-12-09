package com.example;

import com.example.hbutil.HibernateUtil;
import com.example.onetomany.Post;
import com.example.onetomany.PostComment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostCommentApp {

    public static void main(String[] args) {
        savePost();
        getPost();
        removePost();
        HibernateUtil.shutdown();
    }

    private static void savePost() {
        HibernateUtil.getSessionFactory(new Class[]{Post.class, PostComment.class}).inTransaction(session -> session.persist(
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
        HibernateUtil.getSessionFactory(new Class[]{Post.class, PostComment.class}).inTransaction(session -> {
            Post post = session.find(Post.class, 1L);
            PostComment comment = post.getComments().get(0);
            log.info("Comment :: {}", comment);
        });

    }

    private static void removePost() {
        HibernateUtil.getSessionFactory(new Class[]{Post.class, PostComment.class}).inTransaction(session -> {
            Post post = session.find(Post.class, 1L);
            PostComment comment = post.getComments().get(0);

            post.removeComment(comment);
        });
    }
}
