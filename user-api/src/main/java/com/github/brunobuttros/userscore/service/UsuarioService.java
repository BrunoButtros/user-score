package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.dto.*;
import com.github.brunobuttros.userscore.entity.EnderecoEntity;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.exceptions.LoginOuCPFJaExistenteException;
import com.github.brunobuttros.userscore.exceptions.UserIDNotFoundException;
import com.github.brunobuttros.userscore.repository.EnderecoRepository;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import com.github.brunobuttros.userscore.entity.UsuarioRole;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final BuscaCepClient buscaCepClient;
    private final ScoreApiClient scoreApiClient;
    private final EnderecoRepository enderecoRepository;


    public UsuarioService(UsuarioRepository usuarioRepository,
                          BuscaCepClient buscaCepClient,
                          ScoreApiClient scoreApiClient,
                          EnderecoRepository enderecoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.buscaCepClient = buscaCepClient;
        this.scoreApiClient = scoreApiClient;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public UsuarioEntity cadastrar(CadastrarDTO data) {
        logger.debug("Iniciando cadastro de usuário com login: {}", data.login());
        if (usuarioRepository.existsByLogin(data.login()) || usuarioRepository.existsByCpf(data.cpf())) {
            throw new LoginOuCPFJaExistenteException("Usuário com login ou CPF já existente.");
        }

        int score = scoreApiClient.getScore(data.cpf());
        EnderecoEntity enderecoEntity = buscaCepClient.buscarEnderecoPorCep(data.cep());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UsuarioEntity novoUsuario = new UsuarioEntity(data.login(), encryptedPassword, UsuarioRole.USER);
        novoUsuario.setNome(data.nome());
        novoUsuario.setEmail(data.email());
        novoUsuario.setCpf(data.cpf());
        novoUsuario.setTelefone(data.telefone());
        novoUsuario.setCep(data.cep());
        novoUsuario.setScore(score);
        novoUsuario.setEndereco(enderecoEntity);

        return usuarioRepository.save(novoUsuario);
    }

    @Transactional
    public UsuarioEntity atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        logger.debug("Iniciando atualização do usuário com ID: {}", id);
        UsuarioEntity usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserIDNotFoundException("Usuário não encontrado com o ID: " + id));

        if (usuarioDTO.email() != null && !usuarioDTO.email().equals(usuarioExistente.getEmail())) {
            usuarioExistente.setEmail(usuarioDTO.email());
        }
        if (usuarioDTO.telefone() != null && !usuarioDTO.telefone().equals(usuarioExistente.getTelefone())) {
            usuarioExistente.setTelefone(usuarioDTO.telefone());
        }
        if (usuarioDTO.cep() != null && !usuarioDTO.cep().equals(usuarioExistente.getCep())) {
            EnderecoEntity enderecoEntity = buscaCepClient.buscarEnderecoPorCep(usuarioDTO.cep());
            usuarioExistente.setEndereco(enderecoEntity);
            usuarioExistente.setCep(usuarioDTO.cep());
        }

        int score = scoreApiClient.getScore(usuarioExistente.getCpf());
        usuarioExistente.setScore(score);

        usuarioRepository.save(usuarioExistente);
        return usuarioExistente;
    }

    public List<UsuarioDTO> buscarUsuarios(Long id, String nome, String email, String telefone, String cpf) {
        logger.info("Buscando usuários com os parâmetros - id: {}, nome: {}, email: {}, telefone: {}, cpf: {}", id, nome, email, telefone, cpf);

        List<UsuarioEntity> entities;

        if (id == null && nome == null && email == null && telefone == null && cpf == null) {
            entities = usuarioRepository.findAll();
        } else {
            entities = usuarioRepository.findByIdOrNomeOrEmailOrTelefoneOrCpf(id, nome, email, telefone, cpf);
        }
        logger.info("Número de usuários encontrados: {}", entities.size());

        return entities.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    public void deletarUsuario(Long id) {
        logger.debug("Iniciando deleção do usuário com ID: {}", id);
        UsuarioEntity usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado com o ID: {}", id);
                    return new UserIDNotFoundException("Usuário não encontrado com o ID: " + id);
                });

        usuarioRepository.delete(usuarioExistente);
    }

    public UserScoreDTO getUserScoreById(Long id) {
        logger.debug("Iniciando obtenção do score do usuário com ID: {}", id);
        UsuarioEntity usuarioEntity = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    return new UserIDNotFoundException("Usuário não encontrado com o ID: " + id);
                });

        int score = scoreApiClient.getScore(usuarioEntity.getCpf());

        return new UserScoreDTO(usuarioEntity.getId(), score);
    }

    public UsuarioDTO convertEntityToDTO(UsuarioEntity usuarioEntity) {
        EnderecoEntity endereco = usuarioEntity.getEndereco();
        return new UsuarioDTO(
                usuarioEntity.getId(),
                usuarioEntity.getNome(),
                usuarioEntity.getEmail(),
                usuarioEntity.getTelefone(),
                usuarioEntity.getCpf(),
                endereco != null ? endereco.getCep() : null,
                usuarioEntity.getScore(),
                endereco != null ? convertEnderecoToDTO(endereco) : null
        );
    }

    private EnderecoDTO convertEnderecoToDTO(EnderecoEntity enderecoEntity) {
        return new EnderecoDTO(
                enderecoEntity.getId(),
                enderecoEntity.getCep(),
                enderecoEntity.getLogradouro(),
                enderecoEntity.getBairro(),
                enderecoEntity.getLocalidade(),
                enderecoEntity.getUf()
        );
    }
    public List<UserListDTO> getAllUserScores() {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> new UserListDTO(usuario.getNome(), usuario.getScore()))
                .collect(Collectors.toList());
    }
}
