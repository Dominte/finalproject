package com.example.finalproject.utils;

public enum UserPermission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    TEST_SUBMIT("test:submit");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
