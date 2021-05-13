package com.example.finalproject.utils;

import com.google.common.collect.Sets;

import java.util.Set;
import static com.example.finalproject.utils.UserPermission.*;

public enum UserRole {
    STUDENT(Sets.newHashSet(STUDENT_WRITE,STUDENT_READ)),
    TEACHER(Sets.newHashSet(TEST_SUBMIT)),
    ADMIN(Sets.newHashSet());


    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }
}