package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.dto.UsuarioDTO;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;



    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testBuscarUsuarios() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setNome("Test");
        usuario.setEmail("test@example.com");
        usuario.setTelefone("123456789");
        usuario.setCpf("11122233344");

        when(usuarioRepository.findByIdOrNomeOrEmailOrTelefoneOrCpf(1L, "Test", "test@example.com", "123456789", "11122233344"))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> usuarios = usuarioService.buscarUsuarios(1L, "Test", "test@example.com", "123456789", "11122233344");

        assertEquals(1, usuarios.size());
        assertEquals("Test", usuarios.get(0).nome());
    }
}