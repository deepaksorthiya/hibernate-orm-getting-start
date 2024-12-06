package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OneToOneMultiThreadingAppTest {

    private final OneToOneMultiThreadingApp app = new OneToOneMultiThreadingApp();

    @Test
    void main() throws InterruptedException {
        OneToOneMultiThreadingApp.main(null);
        assertNotNull(app);
    }
}