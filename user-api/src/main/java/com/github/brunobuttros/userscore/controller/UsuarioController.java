package com.github.brunobuttros.userscore.controller;

import com.github.brunobuttros.userscore.dto.CadastrarDTO;
import com.github.brunobuttros.userscore.dto.UserListDTO;
import com.github.brunobuttros.userscore.dto.UserScoreDTO;
import com.github.brunobuttros.userscore.dto.UsuarioDTO;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import com.github.brunobuttros.userscore.service.BuscaCepClient;
import com.github.brunobuttros.userscore.service.ScoreApiClient;
import com.github.brunobuttros.userscore.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "basicAuth")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ScoreApiClient scoreApiClient;
    @Autowired
    private BuscaCepClient buscaCepClient;

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody @Validated CadastrarDTO data) {
        try {
            UsuarioEntity novoUsuario = usuarioService.cadastrar(data);
            UsuarioDTO usuarioDTO = usuarioService.convertEntityToDTO(novoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<UsuarioDTO> atualizarCadastro(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioEntity usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDTO);
        UsuarioDTO usuarioAtualizadoDTO = usuarioService.convertEntityToDTO(usuarioAtualizado);
        return ResponseEntity.ok(usuarioAtualizadoDTO);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(@RequestParam(required = false) Long id,
                                                           @RequestParam(required = false) String nome,
                                                           @RequestParam(required = false) String email,
                                                           @RequestParam(required = false) String telefone,
                                                           @RequestParam(required = false) String cpf) {
        List<UsuarioDTO> usuarios = usuarioService.buscarUsuarios(id, nome, email, telefone, cpf);
        return ResponseEntity.ok().body(usuarios);
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<UsuarioDTO> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<UserScoreDTO> getUserScoreById(@PathVariable Long id) {
        UserScoreDTO userScoreDTO = usuarioService.getUserScoreById(id);
        return ResponseEntity.ok(userScoreDTO);
    }

    @GetMapping("/scores")
    public ResponseEntity<List<UserListDTO>> listarNomesEScores() {
        List<UserListDTO> userScores = usuarioService.getAllUserScores();
        return ResponseEntity.ok(userScores);
    }

}