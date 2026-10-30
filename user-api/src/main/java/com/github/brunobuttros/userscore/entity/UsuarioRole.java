package com.github.brunobuttros.userscore.entity;

public enum UsuarioRole {
    ADMIN("admin"),
    USER("user");
    private String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String role(){
        return role;
    }
}
