package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OneToOneAppTest {

    private final OneToOneApp app = new OneToOneApp();

    @Test
    void main() {
        OneToOneApp.main(null);
        assertNotNull(app);
    }
}