package com.soham.reverselogistics.security;

import com.soham.reverselogistics.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/** Small read-only view of the logged-in user, used by controllers and pages. */
public class CurrentUser {

    private final String username;
    private final Role role;
    private final String displayName;

    public CurrentUser(String username, Role role, String displayName) {
        this.username = username;
        this.role = role;
        this.displayName = displayName;
    }

    public static CurrentUser from(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("No authenticated user");
        }
        Role found = Role.CUSTOMER;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String name = authority.getAuthority();
            if (name != null && name.startsWith("ROLE_")) {
                try {
                    found = Role.valueOf(name.substring(5));
                    break;
                } catch (IllegalArgumentException ex) {
                    // not one of our roles, keep looking
                }
            }
        }
        String username = authentication.getName();
        return new CurrentUser(username, found, DemoUsers.displayName(username));
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }

    public boolean isWarehouse() {
        return role == Role.WAREHOUSE;
    }

    public boolean isStaff() {
        return role != Role.CUSTOMER;
    }

    public String getInitials() {
        StringBuilder sb = new StringBuilder();
        for (String part : displayName.trim().split("\\s+")) {
            if (!part.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return sb.length() == 0 ? "U" : sb.toString();
    }
}
