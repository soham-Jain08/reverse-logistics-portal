package com.soham.reverselogistics.security;

import java.util.Map;

/** Names of the demo users, used to show friendly names on screen. */
public final class DemoUsers {

    private static final Map<String, String> NAMES = Map.of(
            "customer1", "Aarav Mehta",
            "customer2", "Priya Nair",
            "manager1", "Neha Kulkarni",
            "warehouse1", "Rahul Deshmukh");

    private DemoUsers() {
    }

    public static String displayName(String username) {
        return NAMES.getOrDefault(username, username);
    }
}
