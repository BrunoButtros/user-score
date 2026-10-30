package com.github.brunobuttros.userscore.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioRoleTest {

    @Test
    public void testUsuarioRoleValues() {
        assertThat(UsuarioRole.valueOf("ADMIN")).isNotNull();
        assertThat(UsuarioRole.valueOf("USER")).isNotNull();
    }

    @Test
    public void testUsuarioRoleAdmin() {
        UsuarioRole adminRole = UsuarioRole.ADMIN;
        assertThat(adminRole.role()).isEqualTo("admin");
    }

    @Test
    public void testUsuarioRoleUser() {
        UsuarioRole userRole = UsuarioRole.USER;
        assertThat(userRole.role()).isEqualTo("user");
    }
}
