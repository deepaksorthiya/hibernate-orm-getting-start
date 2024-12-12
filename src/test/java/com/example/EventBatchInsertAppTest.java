package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventBatchInsertAppTest {
    private final EventBatchInsertApp app = new EventBatchInsertApp();

    @Test
    void main() {
        EventBatchInsertApp.main(null);
        assertNotNull(app);
    }
}