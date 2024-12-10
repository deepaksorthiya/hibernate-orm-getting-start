package com.example;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManyToManyAppUserRoleAppTest {

    private final ManyToManyAppUserRoleApp app = new ManyToManyAppUserRoleApp();

    @Test
    void main() {
        ManyToManyAppUserRoleApp.main(null);
        assertNotNull(app);
    }
}
