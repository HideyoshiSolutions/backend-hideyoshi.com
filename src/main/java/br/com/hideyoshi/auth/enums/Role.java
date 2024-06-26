package br.com.hideyoshi.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    @JsonValue
    private final String description;

    Role(String description) {
        this.description = description;
    }

    public static Role byValue(String description) {
        for (Role r : values()) {
            if (r.getDescription().equals(description)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

    public String getDescription() {
        return this.description;
    }

}