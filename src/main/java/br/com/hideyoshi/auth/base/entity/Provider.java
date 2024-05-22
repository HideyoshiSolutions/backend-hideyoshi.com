package br.com.hideyoshi.auth.base.entity;

import lombok.Getter;

@Getter
public enum Provider {

    GOOGLE("google"),

    GITHUB("github"),

    LOCAL("local");

    private final String name;

    Provider(String name) {
        this.name = name;
    }

    public static Provider byValue(String name) {
        for (Provider p : values()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

}
