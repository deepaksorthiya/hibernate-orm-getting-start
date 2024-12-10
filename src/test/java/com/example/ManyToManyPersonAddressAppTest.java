package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManyToManyPersonAddressAppTest {

    private final ManyToManyPersonAddressApp app = new ManyToManyPersonAddressApp();

    @Test
    void main() {
        ManyToManyPersonAddressApp.main(null);
        assertNotNull(app);
    }
}
