package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OneToManyPostCommentAppTest {

    private final OneToManyPostCommentApp app = new OneToManyPostCommentApp();

    @Test
    void main() {
        OneToManyPostCommentApp.main(null);
        assertNotNull(app);
    }
}