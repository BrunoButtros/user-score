package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.dto.UsuarioDTO;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.exceptions.FileCreationException;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final UsuarioRepository usuarioRepository;
    private final ScoreApiClient scoreApiClient;
    private final EmailService emailService;

    @Value("${app.file.path}")
    private String filePath;

    @Value("${feature.envia.email}")
    private boolean flagMail;

    public FileService(UsuarioRepository usuarioRepository, UsuarioService usuarioService, ScoreApiClient scoreApiClient, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.scoreApiClient = scoreApiClient;
        this.emailService = emailService;
    }

    public void criarArquivoUsuario(List<UsuarioDTO> usuarios) {
        Path filePathPath = Paths.get(filePath);
        Path directoryPath = filePathPath.getParent(); // Obtém o diretório do arquivo

        try {
            // Cria o diretório se não existir
            if (directoryPath != null && Files.notExists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePathPath.toFile()))) {
                for (UsuarioDTO usuario : usuarios) {
                    writer.write("ID: " + usuario.id() + ", score atual: " + usuario.score());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new FileCreationException("Falha ao criar o arquivo '" + filePath + "'", e);
        }
    }

    @Scheduled(fixedDelayString = "${reports.schedule}", initialDelayString = "${reports.schedule}")
    public void atualizarArquivoUsuariosPeriodicamente() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        List<UsuarioEntity> usuariosFromRepository = usuarioRepository.findAll();

        for (UsuarioEntity usuario : usuariosFromRepository) {
            int novoScore = scoreApiClient.getScore(usuario.getCpf());

            usuario.setScore(novoScore);
            usuarioRepository.save(usuario);

            usuarios.add(new UsuarioDTO(usuario.getId(), null, // nome
                    null, // email
                    null, // telefone
                    null, // cpf
                    null, // cep
                    novoScore, // usar o novo score gerado
                    null // endereco
            ));
        }

        criarArquivoUsuario(usuarios);

        if (flagMail) {
            emailService.enviarDocumentoParaAdmins(filePath);
        }
    }
}