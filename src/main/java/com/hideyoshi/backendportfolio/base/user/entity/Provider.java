package com.hideyoshi.backendportfolio.base.user.entity;

public enum Provider {

    GOOGLE("google"),

    LOCAL("local");
    private String name;

    Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
