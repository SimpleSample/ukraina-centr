package com.nagornyi.test.utils;

import com.nagornyi.uc.Role;
import com.nagornyi.uc.entity.User;

public final class UserUtils {
    private UserUtils() {
    }

    public static User createTestUser() {
        User testUser = new User();
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setRole(Role.USER);
        testUser.setEmail("test@test.com");
        return testUser;
    }
}
