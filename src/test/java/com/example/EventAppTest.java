package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventAppTest {
    private final EventApp app = new EventApp();

    @Test
    void main() {
        EventApp.main(null);
        assertNotNull(app);
    }
}