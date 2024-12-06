package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostCommentAppTest {

    private final PostCommentApp app = new PostCommentApp();

    @Test
    void main() {
        PostCommentApp.main(null);
        assertNotNull(app);
    }
}